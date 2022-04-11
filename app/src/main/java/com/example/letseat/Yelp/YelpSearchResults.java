package com.example.letseat.Yelp;

public class YelpSearchResults {
    private String name;
    private String rating;
    private String price;
    private String location;
    private String image;
    YelpSearchResults(String name, String rating, String price, String location, String image){
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.location = location;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getRating() {
        return rating;
    }
}
