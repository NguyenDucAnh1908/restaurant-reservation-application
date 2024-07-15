package com.restaurant_reservation_application.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.restaurant_reservation_application.Adapter.FoodListOrderAdapter;
import com.restaurant_reservation_application.Api.CreateOrder;
import com.restaurant_reservation_application.Constant.AppInfo;
import com.restaurant_reservation_application.Model.Foods;
import com.restaurant_reservation_application.Model.Reservation;
import com.restaurant_reservation_application.Model.TableTypes;
import com.restaurant_reservation_application.Model.Tables;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityReserveBinding;

import org.json.JSONObject;

import java.util.ArrayList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ReserveActivity extends BaseActivity {
    ActivityReserveBinding binding;
    String selectedDate;
    String selectedTime;
    String selectedPerson;
    Tables table;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReserveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getVariablesDateAndTimeAndPerson();
        setVariables();
        getCurrentUserId();
        displayTableTypePrice();
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);
        //ZaloPaySDK.init(AppInfo.APP_ID, AppInfo.MAC_KEY);
    }

    private void setVariables() {
        databaseReference = database.getReference("Reservation");
        binding.reserbeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saveReservationToFirebase(selectedDate, selectedTime, Integer.parseInt(selectedPerson));
                initiateZaloPayPayment();
            }
        });

    }
    private void initiateZaloPayPayment() {

        try {

            int amount = 10000;

            // Call the createOrder method to get necessary data for payment
            CreateOrder orderApi = new CreateOrder();
            JSONObject data = orderApi.createOrder(String.valueOf(amount));
            if (data == null) {
                runOnUiThread(() -> {
                    Toast.makeText(ReserveActivity.this, "Failed to retrieve payment data", Toast.LENGTH_SHORT).show();
                    Log.e("ZaloPay", "Failed to retrieve payment data");
                });
                return;
            }

            Log.d("ZaloPay", "Order data: " + data.toString());

            String code = data.getString("returncode");
            if (code.equals("1")) {
                // Payment data retrieved successfully
                String token = data.getString("zptranstoken");

                Log.d("ZaloPay", "Payment token: " + token);

                // Initiate ZaloPay payment
                ZaloPaySDK.getInstance().payOrder(ReserveActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                        // Handle payment succeeded
                        runOnUiThread(() -> {
                            Toast.makeText(ReserveActivity.this, "Payment successful", Toast.LENGTH_SHORT).show();
                            Log.d("ZaloPay", "Payment succeeded. Transaction ID: " + transactionId);
                            // Optionally, you can proceed with further actions upon successful payment
                            saveReservationToFirebase(selectedDate, selectedTime, Integer.parseInt(selectedPerson));
                        });
                    }

                    @Override
                    public void onPaymentCanceled(String zpTransToken, String appTransID) {
                        // Handle payment cancellation
                        runOnUiThread(() -> {
                            Toast.makeText(ReserveActivity.this, "Payment canceled", Toast.LENGTH_SHORT).show();
                            Log.d("ZaloPay", "Payment canceled");
                            // Optionally, handle cancellation scenarios
                        });
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                        // Handle payment error
                        runOnUiThread(() -> {
                            Toast.makeText(ReserveActivity.this, "Payment error: " + zaloPayError.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("ZaloPay", "Payment error: " + zaloPayError.toString());
                            // Optionally, handle error scenarios
                        });
                    }
                });
            } else {
                // Handle error in retrieving payment data
                runOnUiThread(() -> {
                    Toast.makeText(ReserveActivity.this, "Failed to initiate payment", Toast.LENGTH_SHORT).show();
                    Log.e("ZaloPay", "Failed to initiate payment. Return code: " + code);
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                Toast.makeText(ReserveActivity.this, "Failed to initiate payment", Toast.LENGTH_SHORT).show();
                Log.e("ZaloPay", "Exception while initiating payment: " + e.getMessage());
            });
        }
    }
    private String generateOrderId() {
        // Replace with your logic to generate orderId (unique identifier for each transaction)
        return "Order_" + System.currentTimeMillis();
    }

    private void displayTableTypePrice() {
        if (table != null) {
            DatabaseReference tableTypeRef = FirebaseDatabase.getInstance().getReference("TableTypes").child(String.valueOf(table.getTypeId()));
            tableTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        TableTypes tableType = snapshot.getValue(TableTypes.class);
                        if (tableType != null) {
                            String priceText = tableType.getPrice() + " VND";
                            binding.orderBookingTxt.setText(priceText);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ReserveActivity.this, "Error fetching table type information", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showCartDialog() {
        // Tạo dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_cart); // Inflate layout của CartActivity

        // Các thiết lập và xử lý sự kiện của dialog
//        TextView closeBtn = dialog.findViewById(R.id.closeBtn);
//        closeBtn.setOnClickListener(v -> dialog.dismiss());

        // Hiển thị dialog
        dialog.show();
    }

    private void showMenuDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_list_order_food, null);
        builder.setView(dialogView);

        RecyclerView menuRecyclerView = dialogView.findViewById(R.id.foodsView);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Foods");
        ArrayList<Foods> foodList = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                    Foods food = foodSnapshot.getValue(Foods.class);
                    foodList.add(food);
                }
                FoodListOrderAdapter adapter = new FoodListOrderAdapter(foodList);
                menuRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReserveActivity.this, "Error fetching menu", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void saveReservationToFirebase(String date, String time, int people) {
        String name = binding.fullNameTxt.getText().toString().trim();
        String phoneNumber = binding.phoneNumberTxt.getText().toString().trim();
        //String email = binding.emailTxt.getText().toString().trim(); // Optional field

        // Reset error messages
        binding.fullNameErrorTxt.setVisibility(View.GONE);
        binding.phoneNumberErrorTxt.setVisibility(View.GONE);

        boolean isValid = true;

        // Validate full name
        if (name.isEmpty()) {
            binding.fullNameErrorTxt.setText("Full name is required");
            binding.fullNameErrorTxt.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Validate phone number
        if (phoneNumber.isEmpty()) {
            binding.phoneNumberErrorTxt.setText("Phone number is required");
            binding.phoneNumberErrorTxt.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!phoneNumber.matches("^0[0-9]{9}$")) {
            binding.phoneNumberErrorTxt.setText("Phone number must be 10 digits starting with 0");
            binding.phoneNumberErrorTxt.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (!isValid) {
            return; // Stop further processing if validation fails
        }

        // Reference to ReservationIdCounter node
        DatabaseReference reservationIdReference = database.getReference("ReservationIdCounter");

        // Run transaction to get and increment the reservation ID
        reservationIdReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer currentValue = currentData.getValue(Integer.class);
                if (currentValue == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue(currentValue + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed) {
                    int reservationId = currentData.getValue(Integer.class);

                    // Create a reservation object
                    Reservation reservation = new Reservation(reservationId, 1, time, time, date, name, phoneNumber, people, userId, table.getId());

                    // Save reservation to Firebase
                    databaseReference.child(String.valueOf(reservationId)).setValue(reservation).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showSuccessDialog(reservation);
                        } else {
                            Toast.makeText(ReserveActivity.this, "Failed to save reservation", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Handle error
                    Log.e("Firebase", "Failed to get reservation ID");
                }
            }
        });
    }

    private String generateMac(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacData = mac.doFinal(data.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hmacData) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    private void showSuccessDialog(Reservation reservation) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.success_dialog_reserve, null);

        // Set the data to the dialog views
        TextView nameTxt = dialogView.findViewById(R.id.nameTxt);
        TextView phoneNumberTxt = dialogView.findViewById(R.id.phoneNumberTxt);
        TextView dateAndTimeTxt = dialogView.findViewById(R.id.dateAndTimeTxt);
        TextView personTxt = dialogView.findViewById(R.id.personTxt);
        TextView desTxt = dialogView.findViewById(R.id.desTxt);

        nameTxt.setText(reservation.getName());
        phoneNumberTxt.setText(reservation.getPhoneNumber());
        dateAndTimeTxt.setText(reservation.getDate() + " | " + reservation.getStartTime());
        personTxt.setText(reservation.getPeople() + " Guests");
        //desTxt.setText("Reservation ID: " + reservation.getId());

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set the onClick listener for the Done button
        dialogView.findViewById(R.id.doneBtn).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(ReserveActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
        });
    }

    private void getVariablesDateAndTimeAndPerson() {
        // Retrieve data from intent
        selectedDate = getIntent().getStringExtra("selectedDate");
        selectedTime = getIntent().getStringExtra("selectedTime");
        selectedPerson = getIntent().getStringExtra("selectedPerson");
        table = (Tables) getIntent().getSerializableExtra("table");
        //userId = getIntent().getStringExtra("userId");

        // Set the date and time in the respective TextViews
        binding.dateAndTimeTxt.setText(selectedDate + " | " + selectedTime);
        binding.peopleTxt.setText(selectedPerson);
    }
    private void getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            // Handle user not logged in case
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if user is not logged in
        }
    }
    private int calculateTotalAmount() {
        // Replace with your logic to calculate total amount (in VND) as an int
        String amountText = binding.orderBookingTxt.getText().toString().trim();
        // Remove " VND" from the text and any extra spaces
        String amountString = amountText.replace(" VND", "").trim();

        // Parse the amount string to an integer
        double amountDouble = Double.parseDouble(amountString);
        int amount = (int) amountDouble; // Convert double to int

        return amount;
    }


}

