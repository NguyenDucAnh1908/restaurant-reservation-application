package com.restaurant_reservation_application.Model;

public class FoodOrder {
    private int foodId;
    private String description;
    private String imageName;
    private String name;
    private double price;

    public FoodOrder() {
        // Constructor mặc định cần thiết cho Firebase
    }

    public FoodOrder(int foodId, String description, String imageName, String name, double price) {
        this.foodId = foodId;
        this.description = description;
        this.imageName = imageName;
        this.name = name;
        this.price = price;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}


