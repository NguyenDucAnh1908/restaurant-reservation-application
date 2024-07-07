package com.restaurant_reservation_application.Model;

public class User {
    private String Role;
    private String Email;
    private String PhoneNumber;
    private String Id;
    private String Name;
    private String Password;

    public User(String role, String email, String phoneNumber, String id, String name, String password) {
        Role = role;
        Email = email;
        PhoneNumber = phoneNumber;
        Id = id;
        Name = name;
        Password = password;
    }

    // Empty constructor required for Firebase deserialization
    public User() {
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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
}
