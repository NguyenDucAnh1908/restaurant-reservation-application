package com.restaurant_reservation_application.Model;

import java.io.Serializable;


public class Reservation implements Serializable {
    private int Id;
    private int TransactionId;
    private String StartTime;
    private String EndTime;
    private String Date;
    private String Name;
    private String PhoneNumber;
    private int People;
    private int UserId;
    private int TableId;

    public Reservation() {
    }

    public Reservation(int id, int transactionId, String startTime, String endTime, String date, String name, String phoneNumber, int people, int userId, int tableId) {
        Id = id;
        TransactionId = transactionId;
        StartTime = startTime;
        EndTime = endTime;
        Date = date;
        Name = name;
        PhoneNumber = phoneNumber;
        People = people;
        UserId = userId;
        TableId = tableId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(int transactionId) {
        TransactionId = transactionId;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public int getPeople() {
        return People;
    }

    public void setPeople(int people) {
        People = people;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getTableId() {
        return TableId;
    }

    public void setTableId(int tableId) {
        TableId = tableId;
    }
}


