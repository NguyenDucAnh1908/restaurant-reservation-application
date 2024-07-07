package com.restaurant_reservation_application.Activity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restaurant_reservation_application.Adapter.MessageAdapter;
import com.restaurant_reservation_application.Model.Message;
import com.restaurant_reservation_application.Model.User;
import com.restaurant_reservation_application.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class ChatActivity extends AppCompatActivity {
    private String currentUserId;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private EditText editTextMessage;
    private Button buttonSend;
    private DatabaseReference usersRef;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recycleViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(new ArrayList<>(), this, currentUserId);
        recyclerView.setAdapter(messageAdapter);

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Messages");
        usersRef = database.getReference("Users");

        // Initialize UI components
        editTextMessage = findViewById(R.id.editMsg);
        buttonSend = findViewById(R.id.buttonMsg);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageContent = editTextMessage.getText().toString().trim();
                if (!messageContent.isEmpty()) {
                    sendMessage(messageContent);
                    editTextMessage.setText("");
                }
            }
        });
        getCurrentUserId();
        loadMessages();
    }

    private void loadMessages() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<>();
                String lastDate = "";

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        // Check if sender's ID is the current user's ID
                        if (message.getSender().getId().equals(currentUserId)) {
                            message.setChatMessage(true); // Mark as chat message
                        } else {
                            message.setChatMessage(false); // Mark as reply message
                        }

                        // Determine if showDate should be set
                        if (!message.getDate().equals(lastDate)) {
                            message.setShowDate(true);
                            lastDate = message.getDate();
                        } else {
                            message.setShowDate(false);
                        }

                        messages.add(message);
                    }
                }
                messageAdapter.submitList(messages);

                // Scroll to the last message if the list is not empty
                if (!messages.isEmpty()) {
                    recyclerView.smoothScrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatActivity", "Failed to load messages.", databaseError.toException());
            }
        });
    }


    private void sendMessage(final String content) {
        if (currentUserId == null) {
            Log.e("ChatActivity", "Current user ID is null. Cannot send message.");
            return;
        }

        final long timestamp = System.currentTimeMillis();

        // Parse the timestamp to date and time strings
        SimpleDateFormat sdfDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        final String messageDate = sdfDate.format(new Date(timestamp));

        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm", Locale.getDefault());
        final String messageTimestamp = sdfTime.format(new Date(timestamp));

        // Call fetchSenderInfo to fetch sender details and send message
        fetchSenderInfo(content, messageDate, messageTimestamp);
    }


    private void fetchSenderInfo(final String content, final String messageDate, final String messageTimestamp) {
        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Use Map to handle any unexpected data types
                    Map<String, Object> userMap = (Map<String, Object>) dataSnapshot.getValue();
                    if (userMap != null) {
                        String role = String.valueOf(userMap.get("role"));
                        String email = String.valueOf(userMap.get("email"));
                        String phoneNumber = String.valueOf(userMap.get("phoneNumber"));
                        String id = String.valueOf(userMap.get("id"));
                        String name = String.valueOf(userMap.get("name"));
                        String password = String.valueOf(userMap.get("password"));

                        // Create new User object
                        User sender = new User(role, email, phoneNumber, id, name, password);

                        // Create new Message object with sender info
                        Message newMessage = new Message(content, messageDate, messageTimestamp, sender);

                        // Push new message to Firebase Database
                        databaseReference.push().setValue(newMessage);
                    }
                } else {
                    Log.e("ChatActivity", "User not found: " + "1");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatActivity", "Failed to fetch sender info.", databaseError.toException());
            }
        });
    }

    private void getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        } else {
            // Handle user not logged in case
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if user is not logged in
        }
    }
}
