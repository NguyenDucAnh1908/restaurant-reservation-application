package com.restaurant_reservation_application.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restaurant_reservation_application.Model.ChatRoom;
import com.restaurant_reservation_application.Model.Users;
import com.restaurant_reservation_application.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    private List<ChatRoom> chatRooms;
    private String currentUserId;
    private final OnChatRoomClickListener onChatRoomClickListener;
    private final DatabaseReference usersRef;
    private final Map<String, String> userIdToNameMap = new HashMap<>();

    public interface OnChatRoomClickListener {
        void onChatRoomClick(ChatRoom chatRoom);
    }

    public ChatRoomAdapter(List<ChatRoom> chatRooms, OnChatRoomClickListener onChatRoomClickListener) {
        this.chatRooms = chatRooms;
        this.onChatRoomClickListener = onChatRoomClickListener;
        this.usersRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_row, parent, false);
        return new ChatRoomViewHolder(view, onChatRoomClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        holder.bind(chatRooms.get(position));
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public void submitList(List<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
        notifyDataSetChanged();
    }

    class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        TextView textLastMessage, textTimestamp, textUsername;

        ChatRoomViewHolder(@NonNull View itemView, OnChatRoomClickListener onChatRoomClickListener) {
            super(itemView);
            textLastMessage = itemView.findViewById(R.id.last_message_text);
            textTimestamp = itemView.findViewById(R.id.last_message_time_text);
            textUsername = itemView.findViewById(R.id.user_name_text);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onChatRoomClickListener.onChatRoomClick((ChatRoom) itemView.getTag());
                }
            });
        }

        void bind(ChatRoom chatRoom) {
            textLastMessage.setText(chatRoom.getLastMessage());

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String time = sdf.format(new Date(chatRoom.getLastMessageTimestamp()));
            textTimestamp.setText(time);

            if (userIdToNameMap.containsKey(chatRoom.getUserIds().get(0))) { // Assuming the first user ID is the main one for display
                textUsername.setText(userIdToNameMap.get(chatRoom.getUserIds().get(0)));
            } else {
                textUsername.setText("Loading...");
                fetchUserName(chatRoom.getUserIds().get(0));
            }

            itemView.setTag(chatRoom);
        }

        private void fetchUserName(String userId) {
            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users user = snapshot.getValue(Users.class);
                    if (user != null) {
                        userIdToNameMap.put(userId, user.getName());
                        notifyItemChanged(getAdapterPosition());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error if necessary
                }
            });
        }
    }
}
