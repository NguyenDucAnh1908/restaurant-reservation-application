package com.restaurant_reservation_application.Model;

import java.util.List;

public class FoodOrderList {
    private String orderId;
    private List<FoodOrder> foodOrders;
    private int tableId;
    private double totalTxt;

    public FoodOrderList() {
        // Default constructor required for Firebase
    }

    public FoodOrderList(String orderId, List<FoodOrder> foodOrders, int tableId, double totalTxt) {
        this.orderId = orderId;
        this.foodOrders = foodOrders;
        this.tableId = tableId;
        this.totalTxt = totalTxt;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<FoodOrder> getFoodOrders() {
        return foodOrders;
    }

    public void setFoodOrders(List<FoodOrder> foodOrders) {
        this.foodOrders = foodOrders;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public double getTotalTxt() {
        return totalTxt;
    }

    public void setTotalTxt(double totalTxt) {
        this.totalTxt = totalTxt;
    }
}




