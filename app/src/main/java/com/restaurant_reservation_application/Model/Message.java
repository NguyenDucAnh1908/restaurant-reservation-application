package com.restaurant_reservation_application.Model;

public class Message {
    private String content;
    private String date;
    private String timestamp;
    private User sender;
    private boolean showDate;
    private boolean chatMessage;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String content, String date, String timestamp, User sender) {
        this.content = content;
        this.date = date;
        this.timestamp = timestamp;
        this.sender = sender;
        this.showDate = false;
        this.chatMessage = true;
    }

    // Getters and Setters

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    public boolean isChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(boolean chatMessage) {
        this.chatMessage = chatMessage;
    }
}
