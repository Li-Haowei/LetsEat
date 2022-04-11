package com.example.letseat.Yelp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface YelpService {
    //this is the service class where we send HTTP request through Retrofit to Yelp
    @GET("businesses/search")
    public Call<YelpDataClasses> searchRestaurants(
            @Header("Authorization") String authHeader,
            @Query("term") String term,
            @Query("location") String location
    );
}
