package com.example.letseat.restaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.letseat.R;
import com.example.letseat.Yelp.Yelp;
import com.example.letseat.Yelp.YelpSearchResults;
import com.example.letseat.tools.DoubleClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class RestaurantSearch extends AppCompatActivity {
    private ImageView backBtn, searchBtn, refreshBtn;
    private String getFood, getLocation;
    private final List<RestaurantList> restaurantLists = new ArrayList<>();
    private RestaurantAdapter restaurantAdapter;
    private RecyclerView restaurantRecycleView;
    private EditText searchField;
    private YelpSearchResults[] searchResults;
    private RelativeLayout topBar;
    private Yelp yelp = new Yelp("https://api.yelp.com/v3/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);

        searchField = findViewById(R.id.searchField);
        searchField.setSingleLine();
        searchField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        backBtn = findViewById(R.id.backBtn);
        searchBtn = findViewById(R.id.searchBtn);
        refreshBtn = findViewById(R.id.refreshBtn);
        restaurantRecycleView = findViewById(R.id.restaurantRecyclerView);
        topBar = findViewById(R.id.topBar);

        getFood = getIntent().getStringExtra("food");
        getLocation = getIntent().getStringExtra("location");
        searchResults = yelp.searchRestaurants(getString(R.string.yelpAPIKey), getFood, getLocation);

        restaurantRecycleView.setHasFixedSize(true);
        restaurantRecycleView.setLayoutManager(new LinearLayoutManager(this));
        restaurantAdapter = new RestaurantAdapter(restaurantLists, this);
        restaurantRecycleView.setAdapter(restaurantAdapter);


        if (searchResults!=null) {
            for (int i = 0; i < searchResults.length; i++) {
                YelpSearchResults res = searchResults[i];
                RestaurantList restaurantList = new RestaurantList(res.getName(), res.getImage(), res.getPrice(), res.getRating(), res.getLocation());
                restaurantLists.add(restaurantList);
                restaurantAdapter.updateRestaurantList(restaurantLists);
            }
        }
        BackThread bt = new BackThread();
        backBtn.setOnClickListener(view -> {
            finish();
        });
        refreshBtn.setOnClickListener(view ->{
            bt.run();
        });
        topBar.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {
                restaurantRecycleView.scrollToPosition(0);
            }
        });
        Toast.makeText(this,"Please press refresh page",Toast.LENGTH_SHORT).show();

        searchBtn.setOnClickListener(view -> {
            restaurantAdapter.clear();
            getFood = searchField.getText().toString();
            searchResults = yelp.searchRestaurants(getString(R.string.yelpAPIKey), getFood, getLocation);
            bt.run();
        });

    }
    class BackThread implements Runnable {
        public void run(){
            searchResults = yelp.getResults();
            if (searchResults!=null) {
                for (int i = 0; i < searchResults.length; i++) {
                    YelpSearchResults res = searchResults[i];
                    RestaurantList restaurantList = new RestaurantList(res.getName(), res.getImage(), res.getPrice(), res.getRating(), res.getLocation());
                    restaurantLists.add(restaurantList);
                    restaurantAdapter.updateRestaurantList(restaurantLists);
                }
            }
        }
    }
}