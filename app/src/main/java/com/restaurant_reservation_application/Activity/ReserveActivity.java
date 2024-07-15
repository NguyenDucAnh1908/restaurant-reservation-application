package com.restaurant_reservation_application.Activity;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.restaurant_reservation_application.Model.Foods;
import com.restaurant_reservation_application.Model.Notification;
import com.restaurant_reservation_application.Model.Reservation;
import com.restaurant_reservation_application.Model.TableTypes;
import com.restaurant_reservation_application.Model.Tables;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityReserveBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

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
        fetchNotificationsFromFirebase();
        createNotificationChannel();
        checkAndUpdateNotifications();
    }

    private void setVariables() {
        databaseReference = database.getReference("Reservation");
        binding.reserbeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReservationToFirebase(selectedDate, selectedTime, Integer.parseInt(selectedPerson));
            }
        });
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
                            sendNotificationToUser(userId, "Reservation Successful", "Your reservation has been successfully placed.");
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

    private void sendNotificationToUser(String userId, String title, String message) {
        DatabaseReference notificationRef = database.getReference("Notifications");

        // Get the current timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());

        //String notificationId = notificationRef.push().getKey();
        Random random = new Random();
        int notificationId = random.nextInt(1000);
        // Create a notification object
        Notification notification = new Notification(userId, title, message, timestamp, "unread", notificationId);

        // Generate a unique ID for the notification

        if (notificationId != 0) {
            notificationRef.child(String.valueOf(notificationId)).setValue(notification).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Notification", "Notification sent successfully");
                } else {
                    Log.e("Notification", "Failed to send notification");
                }
            });
        }
    }

    private void checkAndUpdateNotifications() {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");

        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot notificationSnapshot : snapshot.getChildren()) {
                    Notification notification = notificationSnapshot.getValue(Notification.class);
                    if (notification != null && notification.getStatus().equals("unread")) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                            Date notificationTime = sdf.parse(notification.getTimestamp());
                            long currentTime = System.currentTimeMillis();
                            long notificationTimeMillis = notificationTime.getTime();

                            if ((currentTime - notificationTimeMillis) > 3600000) { // 3600000ms = 60 minutes
                                // Update status to "Overtime"
                                notificationRef.child(notificationSnapshot.getKey()).child("status").setValue("Overtime");
                            }
                        } catch (Exception e) {
                            Log.e("NotificationCheck", "Error parsing notification timestamp", e);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotificationCheck", "Error checking notifications", error.toException());
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


    private void fetchNotificationsFromFirebase() {
        DatabaseReference notificationsRef = database.getReference("Notifications");
        notificationsRef.orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Iterate over the snapshot to get the latest notification
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Notification notification = dataSnapshot.getValue(Notification.class);
                        if (notification != null) {
                            // Check if the notification is unread and matches the current user
                            if (notification.getUserId().equals(userId)) {
                                // Display the notification
                                displayNotification(notification);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error fetching notifications: " + error.getMessage());
            }
        });
    }


    private void displayNotification(Notification notification) {
        // Tạo intent rõ ràng cho một activity trong ứng dụng của bạn
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Xây dựng thông báo sử dụng NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.bell_icon) // Icon nhỏ cho thông báo
                .setContentTitle(notification.getTitle()) // Tiêu đề của thông báo
                .setContentText(notification.getMessage()) // Nội dung của thông báo
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Ưu tiên mặc định cho thông báo
                .setContentIntent(pendingIntent) // PendingIntent khi người dùng nhấn vào thông báo
                .setAutoCancel(true); // Tự động huỷ thông báo khi người dùng nhấn vào

        // Phát hành thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notification.getId(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reservation Notifications";
            String description = "Notification for reservation status";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}

