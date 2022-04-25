package com.example.letseat.Yelp;

public class YelpSearchResults {
    //This class will be mainly used to read data from retrofit result and it is accessible from outside of the package
    private String name, rating, price, location, image;
    private YelpCategory[] categories;
    YelpSearchResults(String name, String rating, String price, String location, String image, YelpCategory[] categories){
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.location = location;
        this.image = image;
        this.categories = categories;
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

    public YelpCategory[] getCategories() {return categories;}

}
