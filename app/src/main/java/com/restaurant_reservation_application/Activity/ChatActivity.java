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
import com.restaurant_reservation_application.Model.ChatRoom;
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
    private String chatRoomId;
    private MessageAdapter messageAdapter;
    private DatabaseReference usersRef;
    private DatabaseReference chatRoomsRef;
    private DatabaseReference messagesRef;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        chatRoomsRef = database.getReference("ChatRooms");
        messagesRef = database.getReference("Messages");
        usersRef = database.getReference("Users");

        initializeRecyclerView();
        initializeUI();

        getCurrentUserId();
        getChatRoomId();
        loadMessages();
        fetchOtherUsername();
        binding.backBtn.setOnClickListener(v -> finish());
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
        messagesRef.child(chatRoomId).addValueEventListener(new ValueEventListener() {
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
                        messagesRef.child(chatRoomId).push().setValue(newMessage);

                        chatRoomsRef.child(chatRoomId).child("lastMessageTimestamp").setValue(System.currentTimeMillis());
                        chatRoomsRef.child(chatRoomId).child("lastMessageSenderId").setValue(currentUserId);
                        chatRoomsRef.child(chatRoomId).child("lastMessage").setValue(content);
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

    private void getChatRoomId() {
        chatRoomId = getIntent().getStringExtra("chatRoomId");
        if (chatRoomId == null) {
            createChatRoom();
        }
    }

    private void createChatRoom() {
        chatRoomId = chatRoomsRef.push().getKey();
        List<String> userIds = new ArrayList<>();
        userIds.add(currentUserId);
        ChatRoom chatRoom = new ChatRoom(chatRoomId, userIds, System.currentTimeMillis(), currentUserId, "");
        chatRoomsRef.child(chatRoomId).setValue(chatRoom);
    }

    private void fetchOtherUsername() {
        chatRoomsRef.child(chatRoomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                    if (chatRoom != null) {
                        List<String> userIds = chatRoom.getUserIds();
                        String otherUserId = userIds.get(0).equals(currentUserId) ? userIds.get(1) : userIds.get(0);
                        usersRef.child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Users otherUser = dataSnapshot.getValue(Users.class);
                                    if (otherUser != null) {
                                        binding.otherUsername.setText(otherUser.getName());
                                    } else {
                                        Log.e("ChatActivity", "Other user data is null.");
                                    }
                                } else {
                                    Log.e("ChatActivity", "Other user not found for ID: " + otherUserId);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("ChatActivity", "Failed to fetch other user info.", databaseError.toException());
                            }
                        });
                    } else {
                        Log.e("ChatActivity", "Chat room data is null.");
                    }
                } else {
                    Log.e("ChatActivity", "Chat room not found for ID: " + chatRoomId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatActivity", "Failed to fetch chat room info.", databaseError.toException());
            }
        });
    }
}
