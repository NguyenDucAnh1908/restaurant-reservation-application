package com.restaurant_reservation_application.Model;

public class TableTypes {
    private int Id;
    private String ImagePath;
    private double price;
    private String Type;

    public TableTypes(int id, String imagePath, double price, String type) {
        Id = id;
        ImagePath = imagePath;
        this.price = price;
        Type = type;
    }

    public TableTypes() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
