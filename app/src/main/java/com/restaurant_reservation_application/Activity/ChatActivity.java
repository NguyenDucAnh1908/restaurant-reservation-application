package com.restaurant_reservation_application.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.restaurant_reservation_application.Model.Users;
import com.restaurant_reservation_application.databinding.ActivityChatBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends BaseActivity {
    private ActivityChatBinding binding;
    private String currentUserId;
    private MessageAdapter messageAdapter;
    private DatabaseReference usersRef;
    private DatabaseReference databaseReference;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Messages");
        usersRef = database.getReference("Users");

        initializeRecyclerView();
        initializeUI();

        getCurrentUserId();
        loadMessages();
    }

    private void initializeRecyclerView() {
        recyclerView = binding.recycleViewChat;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(new ArrayList<>());
        recyclerView.setAdapter(messageAdapter);
    }

    private void initializeUI() {
        binding.buttonMsg.setOnClickListener(v -> {
            String messageContent = binding.editMsg.getText().toString().trim();
            if (!messageContent.isEmpty()) {
                sendMessage(messageContent);
                binding.editMsg.setText("");
            }
        });
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
                        message.setChatMessage(message.getSender().getId().equals(currentUserId));

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

        SimpleDateFormat sdfDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        final String messageDate = sdfDate.format(new Date(timestamp));

        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm", Locale.getDefault());
        final String messageTimestamp = sdfTime.format(new Date(timestamp));

        fetchSenderInfo(content, messageDate, messageTimestamp);
    }

    private void fetchSenderInfo(final String content, final String messageDate, final String messageTimestamp) {
        usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Users sender = dataSnapshot.getValue(Users.class);
                    if (sender != null) {
                        Message newMessage = new Message(content, messageDate, messageTimestamp, sender);
                        databaseReference.push().setValue(newMessage);
                    } else {
                        Log.e("ChatActivity", "Sender data is null.");
                    }
                } else {
                    Log.e("ChatActivity", "User not found for ID: " + currentUserId);
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
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
