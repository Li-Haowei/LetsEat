package com.example.letseat.restaurant;

public class RestaurantList {
    private String name, imgUrl, price, rating, location;
    private String[] photos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getLocation() {
        return location;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public RestaurantList(String name, String imgUrl, String price, String rating, String location, String[] photos) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.price = price;
        this.rating = rating;
        this.location = location;
        this.photos = photos;
    }
}
