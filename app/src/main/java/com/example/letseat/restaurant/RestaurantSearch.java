package com.example.letseat.restaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.letseat.R;
import com.example.letseat.Yelp.Yelp;
import com.example.letseat.Yelp.YelpSearchResults;

import java.util.ArrayList;
import java.util.List;


public class RestaurantSearch extends AppCompatActivity {
    private ImageView backBtn, searchBtn;
    private final List<RestaurantList> restaurantLists = new ArrayList<>();
    private RestaurantAdapter restaurantAdapter;
    private RecyclerView restaurantRecycleView;
    private YelpSearchResults[] searchResults;
    private Yelp yelp = new Yelp("https://api.yelp.com/v3/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);

        backBtn = findViewById(R.id.backBtn);
        searchBtn = findViewById(R.id.searchBtn);
        restaurantRecycleView = findViewById(R.id.restaurantRecyclerView);

        final String getFood = getIntent().getStringExtra("food");
        final String getLocation = getIntent().getStringExtra("location");
        searchResults = yelp.searchRestaurants(getString(R.string.yelpAPIKey), getFood, getLocation);
        searchResults = yelp.getResults();
        restaurantRecycleView.setHasFixedSize(true);
        restaurantRecycleView.setLayoutManager(new LinearLayoutManager(this));
        restaurantAdapter = new RestaurantAdapter(restaurantLists, this);
        restaurantRecycleView.setAdapter(restaurantAdapter);
        if (searchResults != null) {
            for (int i = 0; i < searchResults.length; i++) {
                YelpSearchResults res = searchResults[i];
                RestaurantList restaurantList = new RestaurantList(res.getName(), res.getImage(), res.getPrice(), res.getRating(), res.getLocation());
                restaurantLists.add(restaurantList);
                restaurantAdapter.updateRestaurantList(restaurantLists);
            }
        }
        backBtn.setOnClickListener(view -> {
            finish();
        });
    }
}