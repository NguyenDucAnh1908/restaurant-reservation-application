package com.restaurant_reservation_application.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restaurant_reservation_application.Adapter.FoodListAdapter;
import com.restaurant_reservation_application.Model.Foods;
import com.restaurant_reservation_application.Model.Reservation;
import com.restaurant_reservation_application.Model.Restaurents;
import com.restaurant_reservation_application.Model.Tables;
import com.restaurant_reservation_application.R;
import com.restaurant_reservation_application.databinding.ActivityRestaurentDetailBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class RestaurentDetailActivity extends BaseActivity {
    ActivityRestaurentDetailBinding binding;
    private Restaurents object;
    private String selectedDate;
    private String selectedTime;
    private String selectedPerson;
    private int tableTypeId; // 0 for Normal, 1 for VIP
    private CheckBox cbNormal, cbVip;
    private DatabaseReference databaseReference;
    private List<Tables> bookedTables = new ArrayList<>();

    private List<Tables> availableTables = new ArrayList<>();
     ArrayList<Foods> menuItems;


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
        getDateAndTime();
        getPerson();
        setupCheckBoxes();
        setupShowMenuButton();
    }

    private void setupShowMenuButton() {
        binding.showMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuDialog();
            }
        });
    }

    private void showMenuDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_list_foods, null);
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
                FoodListAdapter adapter = new FoodListAdapter(foodList);
                menuRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RestaurentDetailActivity.this, "Error fetching menu", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Other existing methods


    private void setupCheckBoxes() {
        cbNormal = findViewById(R.id.cbNormal);
        cbVip = findViewById(R.id.cbVip);

        cbNormal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbVip.setChecked(false);
                    tableTypeId = 0; // Normal selected
                }
            }
        });

        cbVip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbNormal.setChecked(false);
                    tableTypeId = 1; // VIP selected
                }
            }
        });
    }

    private void getPerson() {
    }

    private void getDateAndTime() {
        binding.findSlotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkReservations();
            }
        });
    }


    private void checkReservations() {
        DatabaseReference reservationRef = FirebaseDatabase.getInstance().getReference("Reservation");
        reservationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Integer> bookedTableIds = new ArrayList<>();
                for (DataSnapshot reservationSnapshot : dataSnapshot.getChildren()) {
                    Reservation reservation = reservationSnapshot.getValue(Reservation.class);

                    // Parse startTime to determine the end time (1h30 later)
                    String startTime = reservation.getStartTime();
                    String endTime = calculateEndTime(startTime);

                    // Check if selectedTime is within the range from startTime to endTime
                    if (isWithinTimeRange(selectedTime, startTime, endTime) && reservation.getDate().equals(selectedDate)) {
                        bookedTableIds.add(reservation.getTableId());
                    }
                }

                // After fetching booked table IDs, filter by TableType and Restaurant ID
                getTablesFromIds(bookedTableIds, object.getId(), tableTypeId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RestaurentDetailActivity.this, "Error checking reservations", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getTablesFromIds(List<Integer> bookedTableIds, int restaurantId, int tableTypeId) {
        DatabaseReference tableRef = FirebaseDatabase.getInstance().getReference("Tables");
        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookedTables.clear();
                availableTables.clear();

                for (DataSnapshot tableSnapshot : dataSnapshot.getChildren()) {
                    Tables table = tableSnapshot.getValue(Tables.class);

                    // Check if the table belongs to the selected restaurant and type
                    if (table.getRestaurantId() == restaurantId && table.getTypeId() == tableTypeId) {
                        // Check if the table is in the bookedTables list
                        if (bookedTableIds.contains(table.getId())) {
                            bookedTables.add(table);
                        } else {
                            availableTables.add(table);
                        }
                    }
                }

                // Proceed to show available tables or handle accordingly
                if (availableTables.isEmpty()) {
                    //Toast.makeText(RestaurentDetailActivity.this, "No available tables for this time slot", Toast.LENGTH_SHORT).show();
                    MotionToast.Companion.darkToast(RestaurentDetailActivity.this,
                            "Not found ☹️",
                            "No available tables for this time slot!",
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(RestaurentDetailActivity.this, www.sanju.motiontoast.R.font.montserrat_bold));
                } else {
                    proceedToReserve();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RestaurentDetailActivity.this, "Error fetching tables", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String calculateEndTime(String startTime) {
        // Parse startTime to hours and minutes
        String[] parts = startTime.split(":");
        int startHour = Integer.parseInt(parts[0]);
        int startMinute = Integer.parseInt(parts[1]);

        // Calculate 1h30 later
        int endHour = startHour + 1;
        int endMinute = startMinute + 29;

        if (endMinute >= 60) {
            endHour += 1;
            endMinute -= 60;
        }

        return String.format("%02d:%02d", endHour, endMinute);
    }

    private boolean isWithinTimeRange(String selectedTime, String startTime, String endTime) {
        // Convert time strings to minutes for comparison
        int selectedMinutes = timeToMinutes(selectedTime);
        int startMinutes = timeToMinutes(startTime);
        int endMinutes = timeToMinutes(endTime);

        // Check if selectedTime is between startTime and endTime
        return selectedMinutes >= startMinutes && selectedMinutes <= endMinutes;
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    private void proceedToReserve() {
        Intent intent = new Intent(RestaurentDetailActivity.this, ReserveActivity.class);
        intent.putExtra("selectedDate", selectedDate);
        intent.putExtra("selectedTime", selectedTime);
        intent.putExtra("selectedPerson", selectedPerson);
        intent.putExtra("tableTypeId", tableTypeId);
        intent.putExtra("table", availableTables.get(0));
        startActivity(intent);
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
                    selectedPerson = selectedPeople; //
                    binding.personViewTxt.setText(selectedPerson);
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
                    selectedPerson = customPeople; //
                    binding.personViewTxt.setText(selectedPerson);
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
                selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                binding.timeViewTxt.setText(selectedTime);
            }
        }, hour, minute, true); // Set 'true' for 24-hour time format, 'false' for AM/PM format
        dialog.show();
    }

    private void setPickDate() {
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
                selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                binding.dateViewTxt.setText(selectedDate);
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