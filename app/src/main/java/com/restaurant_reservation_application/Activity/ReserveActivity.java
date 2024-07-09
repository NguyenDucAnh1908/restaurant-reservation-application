package com.restaurant_reservation_application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.restaurant_reservation_application.Api.CreateOrder;
import com.restaurant_reservation_application.Model.Reservation;
import com.restaurant_reservation_application.Model.TableTypes;
import com.restaurant_reservation_application.Model.Tables;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityReserveBinding;

import org.json.JSONObject;

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
    Button btnReserve;
    TableTypes tableType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReserveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnReserve= (Button) findViewById(R.id.reserbeBtn);
        getVariablesDateAndTimeAndPerson();
        setVariables();
        getCurrentUserId();
      //  checkoutWithZaloPay();;
    }

    private void setVariables() {
        databaseReference = database.getReference("Reservation");
        binding.reserbeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReservationToFirebase(selectedDate, selectedTime, Integer.parseInt(selectedPerson));
                checkoutWithZaloPay();
            }
        });
    }
    private void checkoutWithZaloPay() {
        double totalPrice = calculateTotalPrice(); // Tính toán tổng số tiền cần thanh toán

        // Gọi ZaloPay SDK để thanh toán
        ZaloPaySDK.getInstance().payOrder(ReserveActivity.this, String.valueOf(totalPrice), "demozpdk://app", new PayOrderListener() {

            public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                Toast.makeText(ReserveActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                saveReservationToFirebase(selectedDate, selectedTime, Integer.parseInt(selectedPerson));
            }
            public void onPaymentCanceled(String zpTransToken, String appTransID) {

                Toast.makeText(ReserveActivity.this, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show();
            }


            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {

                Toast.makeText(ReserveActivity.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
    private void saveReservationToFirebase(String date, String time, int people) {
        String name = binding.fullNameTxt.getText().toString().trim();
        String phoneNumber = binding.phoneNumberTxt.getText().toString().trim();
        String email = binding.emailTxt.getText().toString().trim(); // Optional field

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
    private double calculateTotalPrice() {

        double price = 0.0;
        // Example logic to find tableType based on table.getTypeId()
        if (tableType != null && tableType.getId() == table.getTypeId()) {
            price = tableType.getPrice();
        }
        return price;
    }


}

