package com.restaurant_reservation_application.Model;

public class BannerModel {
    private int Id;
    private String Url;

    public BannerModel(int id, String url) {
        Id = id;
        Url = url;
    }

    public BannerModel() {
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
