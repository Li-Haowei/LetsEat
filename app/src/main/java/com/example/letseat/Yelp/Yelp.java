package com.example.letseat.Yelp;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
    This will mainly be used from outside, by calling Yelp(Base_URL), which creates a Yelp Retrofit object that consume API.
    The only public function is searchRestaurants(API key, food, location) and returns a yelp result class
     */
public class Yelp {

    //private final CountDownLatch latch = new CountDownLatch(1);
    //final ExecutorService executorService = Executors.newCachedThreadPool();
    private YelpSearchResults[] results;
    private String BASE_URL;
    private Retrofit retrofit;
    private YelpService yp;
    private Call<YelpDataClasses> callAsync;

    //This is the constructor that allows user to construct a yelp instance
    /*
    @params : (String) base url
    @return : None
     */
    public Yelp(String BASE_URL){
        this.BASE_URL = BASE_URL;
        setRetrofit();
    }

    //This method is used to instantiate a retrofit object based on Base url(here is for Yelp).
    private void setRetrofit(){
        this.retrofit = new Retrofit.Builder() //build a new Retrofit class
                .baseUrl(BASE_URL) //Base url, all Yelp fusion API endpoints
                .addConverterFactory(GsonConverterFactory.create()) //This is the adapter that converts JSON data into GSON which can be read and write by Java(not the only way)
                .build();
        //retrofit will search the information wanted from YelpService.class
        yp = retrofit.create(YelpService.class);
    }

    //This is an async method to search restaurants, app does not need to wait for returns
    /*
    @params : API_KEY
    @params : Food
    @params : Location
    @return : YelpSearchResults[]
     */
    public YelpSearchResults[] searchRestaurantsAsync(String API_KEY, String food, String location){
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
                            response.body().restaurants[i].imageUrl,
                            response.body().restaurants[i].categories
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

    //This is an sync method to search restaurants, app has to wait for returns
    /*
    @params : API_KEY
    @params : Food
    @params : Location
    @return : YelpSearchResults[]
     */
    public YelpSearchResults[] searchRestaurantsSync(String API_KEY, String food, String location){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        callAsync = yp.searchRestaurants("Bearer " + API_KEY,food,location);
        try{
            Response<YelpDataClasses> response = callAsync.execute();
            results = new YelpSearchResults[response.body().restaurants.length];
            for (int i = 0; i < response.body().restaurants.length; i++) {
                //Log.d("creation","running: " + response.body().restaurants[i].categories[0].title);
                results[i] = new YelpSearchResults(
                        response.body().restaurants[i].name,
                        response.body().restaurants[i].rating,
                        response.body().restaurants[i].price,
                        response.body().restaurants[i].location.address,
                        response.body().restaurants[i].imageUrl,
                        response.body().restaurants[i].categories
                );
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return results;

    }

    //This is used to return search results
    /*
    @params : None
    @return : YelpSearchResults[]
     */
    public YelpSearchResults[] getResults(){
        return results;
    }

}
