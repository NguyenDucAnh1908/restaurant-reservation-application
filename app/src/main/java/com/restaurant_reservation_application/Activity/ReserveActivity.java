package com.restaurant_reservation_application.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.restaurant_reservation_application.Model.Reservation;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityReserveBinding;

public class ReserveActivity extends BaseActivity {
    ActivityReserveBinding binding;
    String selectedDate;
    String selectedTime;
    String selectedPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReserveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getVariablesDateAndTimeAndPerson();
        setVariables();
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

    private void saveReservationToFirebase(String date, String time, int people) {
        String name = binding.fullNameTxt.getText().toString();
        String phoneNumber = binding.phoneNumberTxt.getText().toString();
        String email = binding.emailTxt.getText().toString(); // Optional field

        // Generate unique ID for the reservation
        String reservationId = databaseReference.push().getKey();

        // Create a reservation object
        Reservation reservation = new Reservation(reservationId, 1, time, time, date, name, phoneNumber, people, 1, 1);

        if (reservationId != null) {
            databaseReference.child(reservationId).setValue(reservation).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showSuccessDialog(reservation);
                } else {
                    Toast.makeText(ReserveActivity.this, "Failed to save reservation", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
        desTxt.setText("Reservation ID: " + reservation.getId());

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set the onClick listener for the Done button
        dialogView.findViewById(R.id.doneBtn).setOnClickListener(v -> dialog.dismiss());
    }

    private void getVariablesDateAndTimeAndPerson() {
        // Retrieve data from intent
        selectedDate = getIntent().getStringExtra("selectedDate");
        selectedTime = getIntent().getStringExtra("selectedTime");
        selectedPerson = getIntent().getStringExtra("selectedPerson");

        // Set the date and time in the respective TextViews
        binding.dateAndTimeTxt.setText(selectedDate + " | " + selectedTime);
        binding.peopleTxt.setText(selectedPerson);
    }
}