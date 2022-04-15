package com.example.letseat.Yelp;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Yelp {
    /*
    This will mainly be used from outside, by calling Yelp(Base_URL), which creates a Yelp Retrofit object that consume API.
    The only public function is searchRestaurants(API key, food, location) and returns a yelp result class
     */
    private YelpSearchResults[] results;
    private String BASE_URL;
    private Retrofit retrofit;
    private YelpService yp;
    private Call<YelpDataClasses> callAsync;
    public Yelp(String BASE_URL){
        this.BASE_URL = BASE_URL;
        setRetrofit();
    }
    private void setRetrofit(){
        this.retrofit = new Retrofit.Builder() //build a new Retrofit class
                .baseUrl(BASE_URL) //Base url, all Yelp fusion API endpoints
                .addConverterFactory(GsonConverterFactory.create()) //This is the adapter that converts JSON data into GSON which can be read and write by Java(not the only way)
                .build();
        yp = retrofit.create(YelpService.class);
    }
    public YelpSearchResults[] searchRestaurants(String API_KEY, String food, String location){
        callAsync = yp.searchRestaurants("Bearer " + API_KEY,food,location);
        callAsync.enqueue(new Callback<YelpDataClasses>() {
            @Override
            public void onResponse(Call<YelpDataClasses> call, Response<YelpDataClasses> response) {
                results = new YelpSearchResults[response.body().restaurants.length];
                for (int i = 0; i < response.body().restaurants.length; i++) {
                    results[i] = new YelpSearchResults(
                                response.body().restaurants[i].name,
                                response.body().restaurants[i].rating,
                                response.body().restaurants[i].price,
                                response.body().restaurants[i].location.address,
                                response.body().restaurants[i].imageUrl
                    );
                }
            }

            @Override
            public void onFailure(Call<YelpDataClasses> call, Throwable t) {
                Log.d("creation", "onFail " + t); //debug purpose
            }
        });
        return results;
    }
    public YelpSearchResults[] getResults(){
        return results;
    }
}
