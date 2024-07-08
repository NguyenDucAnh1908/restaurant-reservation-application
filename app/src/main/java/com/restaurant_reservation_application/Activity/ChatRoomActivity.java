package com.restaurant_reservation_application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
import com.restaurant_reservation_application.Adapter.ChatRoomAdapter;
import com.restaurant_reservation_application.Model.ChatRoom;
import com.restaurant_reservation_application.Model.Users;
import com.restaurant_reservation_application.databinding.ActivityChatRoomBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatRoomActivity extends BaseActivity {
    private ActivityChatRoomBinding binding;
    private String currentUserId;
    private ChatRoomAdapter chatRoomAdapter;
    private DatabaseReference chatRoomsRef;
    private DatabaseReference usersRef;
    private Set<String> userIdsWithRoleZero = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        chatRoomsRef = database.getReference("ChatRooms");
        usersRef = database.getReference("Users");

        initializeRecyclerView();
        getCurrentUserId();
        loadUsersWithRoleZero();
        loadChatRooms();
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void initializeRecyclerView() {
        RecyclerView recyclerView = binding.searchUserRecyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chatRoomAdapter = new ChatRoomAdapter(new ArrayList<>(), chatRoom -> {
            Intent intent = new Intent(ChatRoomActivity.this, ChatActivity.class);
            intent.putExtra("chatRoomId", chatRoom.getChatroomId());
            startActivity(intent);
        });
        recyclerView.setAdapter(chatRoomAdapter);
    }

    private void getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        } else {
            Log.e("ChatRoomActivity", "User not logged in");
            finish();
        }
    }

    private void loadUsersWithRoleZero() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userIdsWithRoleZero.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);
                    if (user != null && user.getRole() == 0) {
                        userIdsWithRoleZero.add(user.getId());
                    }
                }
                loadChatRooms();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatRoomActivity", "Failed to load users.", databaseError.toException());
            }
        });
    }

    private void loadChatRooms() {
        chatRoomsRef.orderByChild("lastMessageTimestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ChatRoom> chatRooms = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatRoom chatRoom = snapshot.getValue(ChatRoom.class);
                    if (chatRoom != null && containsRoleZeroUser(chatRoom.getUserIds())) {
                        chatRooms.add(chatRoom);
                    }
                }
                chatRoomAdapter.submitList(chatRooms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChatRoomActivity", "Failed to load chat rooms.", databaseError.toException());
            }
        });
    }

    private boolean containsRoleZeroUser(List<String> userIds) {
        for (String userId : userIds) {
            if (userIdsWithRoleZero.contains(userId)) {
                return true;
            }
        }
        return false;
    }

}
