package com.restaurant_reservation_application.Model;

public class FoodOrder {
    private String foodOrderId;
    private int foodId;
    private int tableId;

    public FoodOrder() {
        // Default constructor required for calls to DataSnapshot.getValue(FoodOrder.class)
    }

    public FoodOrder(String foodOrderId, int foodId, int tableId) {
        this.foodOrderId = foodOrderId;
        this.foodId = foodId;
        this.tableId = tableId;
    }

    public String getFoodOrderId() {
        return foodOrderId;
    }

    public void setFoodOrderId(String foodOrderId) {
        this.foodOrderId = foodOrderId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }
}

