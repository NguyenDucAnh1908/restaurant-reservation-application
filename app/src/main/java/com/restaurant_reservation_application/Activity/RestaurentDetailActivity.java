package com.restaurant_reservation_application.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.restaurant_reservation_application.Model.Restaurents;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityRestaurentDetailBinding;

import java.util.Calendar;

public class RestaurentDetailActivity extends BaseActivity {
    ActivityRestaurentDetailBinding binding;
    private Restaurents object;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntenExtra();
        setVariable();
        setPickDate();
        setPickTime();
        setPickPerson();
    }

    private void setPickPerson() {
        binding.personPickTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPersonPickerDialog();
            }
        });
    }
    private void showPersonPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Number of People");

        String[] peopleArray = {"1", "2", "3", "4", "5", "More"};

        builder.setItems(peopleArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedPeople = peopleArray[which];
                if (selectedPeople.equals("More")) {
                    showCustomPeopleInputDialog();
                } else {
                    binding.personViewTxt.setText(selectedPeople);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showCustomPeopleInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Number of People");

        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String customPeople = input.getText().toString();
                if (!customPeople.isEmpty()) {
                    binding.personViewTxt.setText(customPeople);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void setPickTime() {
        binding.timePickTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialog();
            }
        });
    }

    private void openTimeDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                binding.timeViewTxt.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        }, hour, minute, true); // Set 'true' for 24-hour time format, 'false' for AM/PM format
        dialog.show();
    }


    private void setPickDate() {
         TextView datePickTxt, dateViewTxt;
        //datePickTxt = findViewById(R.id.datePickBtn);
        //dateViewTxt = findViewById(R.id.dateViewTxt);
        binding.datePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialog();
            }
        });
    }

    private void openDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                binding.dateViewTxt.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(month) + "/" + String.valueOf(year));
            }
        }, year, month, day);
        dialog.show();
    }

    private void setVariable() {
        binding.titleTxt.setText(object.getName());
        binding.descriptionTxt.setText(object.getDescription());
        binding.locationTxt.setText(object.getAddress());
        binding.openCloseTxt.setText(object.getOpen() + " - " + object.getClose());
        Glide.with(this)
                .load(object.getImage())
                .into(binding.pic);
        binding.backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void getIntenExtra() {
        object = (Restaurents) getIntent().getSerializableExtra("object");
    }
}