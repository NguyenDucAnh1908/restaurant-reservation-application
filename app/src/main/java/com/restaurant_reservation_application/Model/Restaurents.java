package com.restaurant_reservation_application.Model;


import java.io.Serializable;

public class Restaurents implements Serializable {
    private int Id;
    private String Address;
    private String Close;
    private String Description;
    private String Image;
    private String Name;
    private String Open;
    private String PhoneNumber;
    private double Score;

    public Restaurents(int id, String address, String close, String description, String image, String name, String open, String phoneNumber, double score) {
        Id = id;
        Address = address;
        Close = close;
        Description = description;
        Image = image;
        Name = name;
        Open = open;
        PhoneNumber = phoneNumber;
        Score = score;
    }


    public Restaurents() {
    }

    public double getScore() {
        return Score;
    }

    public void setScore(double score) {
        Score = score;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getClose() {
        return Close;
    }

    public void setClose(String close) {
        Close = close;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getOpen() {
        return Open;
    }

    public void setOpen(String open) {
        Open = open;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }
}
