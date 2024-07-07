package com.restaurant_reservation_application.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.restaurant_reservation_application.Model.Message;
import com.restaurant_reservation_application.R;

import java.util.List;
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_CHAT = 0;
    private static final int VIEW_TYPE_REPLY = 1;
    private List<Message> messages;
    private Context context;
    private String currentUserId;

    public MessageAdapter(List<Message> messages, Context context, String currentUserId) {
        this.messages = messages;
        this.context = context;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.isChatMessage()) {
            return VIEW_TYPE_CHAT;
        } else {
            return VIEW_TYPE_REPLY;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case VIEW_TYPE_CHAT:
                view = inflater.inflate(R.layout.message_item_chat, parent, false);
                return new ChatViewHolder(view);
            case VIEW_TYPE_REPLY:
                view = inflater.inflate(R.layout.message_item_reply, parent, false);
                return new ReplyViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_CHAT:
                ((ChatViewHolder) holder).bind(message);
                break;
            case VIEW_TYPE_REPLY:
                ((ReplyViewHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void submitList(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textDate, textTimestamp, txtUserName;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.textChatUser);
            textMessage = itemView.findViewById(R.id.textChatMessage);
            textDate = itemView.findViewById(R.id.textChatDate);
            textTimestamp = itemView.findViewById(R.id.textChatTimestamp);
        }

        public void bind(Message message) {
            textMessage.setText(message.getContent());
            textTimestamp.setText(message.getTimestamp());
            if (message.isShowDate()) {
                textDate.setText(message.getDate());
                textDate.setVisibility(View.VISIBLE);
            } else {
                textDate.setVisibility(View.GONE);
            }
        }
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textDate, textTimestamp, txtUserName;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.textReplyUser);
            textMessage = itemView.findViewById(R.id.textReplyMessage);
            textDate = itemView.findViewById(R.id.textReplyDate);
            textTimestamp = itemView.findViewById(R.id.textReplyTimestamp);
        }

        public void bind(Message message) {
            textMessage.setText(message.getContent());
            textTimestamp.setText(message.getTimestamp());
            if (message.isShowDate()) {
                textDate.setText(message.getDate());
                textDate.setVisibility(View.VISIBLE);
            } else {
                textDate.setVisibility(View.GONE);
            }
        }
    }
}
