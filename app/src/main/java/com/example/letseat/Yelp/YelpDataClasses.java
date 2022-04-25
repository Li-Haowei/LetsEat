package com.example.letseat.Yelp;

import com.google.gson.annotations.SerializedName;

//This is the class where we parse the Gson file, the response
public class YelpDataClasses {
    @SerializedName("total") int total;
    @SerializedName("businesses") YelpRestaurants[] restaurants;
}

class YelpRestaurants{
    @SerializedName("name") String name;
    @SerializedName("rating") String rating;
    @SerializedName("price") String price;
    @SerializedName("review_count") int numReviews;
    @SerializedName("image_url") String imageUrl;
    @SerializedName("categories") YelpCategory[] categories;
    @SerializedName("location") YelpLocation location;
    @SerializedName("photos") String[] photos;
}

class YelpLocation{
    @SerializedName("address1") String address;
}