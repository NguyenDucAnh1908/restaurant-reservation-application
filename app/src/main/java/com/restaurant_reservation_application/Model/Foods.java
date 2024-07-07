package com.restaurant_reservation_application.Model;

import java.io.Serializable;

public class Foods implements Serializable {
    private int Id;
    private String Description;
    private String Image;
    private String Name;
    private double Price;

    public Foods() {
    }

    public Foods(int id, String description, String image, String name, double price) {
        Id = id;
        Description = description;
        Image = image;
        Name = name;
        Price = price;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
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

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }
}
