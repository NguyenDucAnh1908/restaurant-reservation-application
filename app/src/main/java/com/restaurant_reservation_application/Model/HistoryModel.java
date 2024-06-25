package com.restaurant_reservation_application.Model;

public class HistoryModel {

    private String restaurantName;
    private String timeAgo;
    private String status;
    private String cancelBooking;
    private String dateTime;
    private String guests;

    public HistoryModel(String restaurantName, String timeAgo, String status, String cancelBooking, String dateTime, String guests) {
        this.restaurantName = restaurantName;
        this.timeAgo = timeAgo;
        this.status = status;
        this.cancelBooking = cancelBooking;
        this.dateTime = dateTime;
        this.guests = guests;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public String getStatus() {
        return status;
    }

    public String getCancelBooking() {
        return cancelBooking;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getGuests() {
        return guests;
    }
}

