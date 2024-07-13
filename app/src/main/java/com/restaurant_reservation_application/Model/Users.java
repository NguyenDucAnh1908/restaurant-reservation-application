package com.restaurant_reservation_application.Model;

import java.io.Serializable;

public class Users implements Serializable {
    private String Id;
    private String Email;
    private String Name;
    private String Password;
    private String PhoneNumber;
    private int Role;

    public Users() {
    }

    public Users(String id, String email, String name, String password, String phoneNumber, int role) {
        Id = id;
        Email = email;
        Name = name;
        Password = password;
        PhoneNumber = phoneNumber;
        Role = role;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }
}
