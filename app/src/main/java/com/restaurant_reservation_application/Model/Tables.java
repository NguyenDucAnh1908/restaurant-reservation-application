package com.restaurant_reservation_application.Model;

import java.io.Serializable;

public class Tables implements Serializable {
    private Integer Id;
    private Integer MaxGuest;
    private Integer MinGuest;
    private String Name;
    private Integer RestaurantId;
    private String Status;
    private Integer TypeId;

    public Tables() {
    }

    public Tables(Integer id, Integer maxGuest, Integer minGuest, String name, Integer restaurantId, String status, Integer typeId) {
        this.Id = id;
        this.MaxGuest = maxGuest;
        this.MinGuest = minGuest;
        this.Name = name;
        this.RestaurantId = restaurantId;
        this.Status = status;
        this.TypeId = typeId;
    }

    // Getter và Setter cho mỗi trường

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Integer getMaxGuest() {
        return MaxGuest;
    }

    public void setMaxGuest(Integer maxGuest) {
        MaxGuest = maxGuest;
    }

    public Integer getMinGuest() {
        return MinGuest;
    }

    public void setMinGuest(Integer minGuest) {
        MinGuest = minGuest;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getRestaurantId() {
        return RestaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        RestaurantId = restaurantId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Integer getTypeId() {
        return TypeId;
    }

    public void setTypeId(Integer typeId) {
        TypeId = typeId;
    }

}

