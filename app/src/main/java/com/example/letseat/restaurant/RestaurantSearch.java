package com.example.letseat.restaurant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.letseat.BuildConfig;
import com.google.android.libraries.places.api.Places;
import com.example.letseat.R;
import com.example.letseat.Yelp.Yelp;
import com.example.letseat.Yelp.YelpSearchResults;
import com.example.letseat.tools.DoubleClickListener;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/*
This class will be working on restaurant search
 */

public class RestaurantSearch extends AppCompatActivity {
    private ImageView backBtn, searchBtn, refreshBtn;
    private String getFood, getLocation, getEmail;
    private final List<RestaurantList> restaurantLists = new ArrayList<>();
    private RestaurantAdapter restaurantAdapter;
    private RecyclerView restaurantRecycleView;
    private EditText searchField;
    private YelpSearchResults[] searchResults;
    private RelativeLayout topBar;
    private final CountDownLatch latch = new CountDownLatch(1);

    //This will be the endpoint for yelp API
    private Yelp yelp = new Yelp("https://api.yelp.com/v3/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);
        //get Yelp_API_Key from local
        Places.initialize(this,BuildConfig.YELP_API_KEY);

        //Bind views to fields
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
        // Temp Solution: pass user email to post
        // Added by Zack
        getEmail = getIntent().getStringExtra("email");
        searchResults = yelp.searchRestaurantsSync(BuildConfig.YELP_API_KEY, getFood, getLocation);
        String email = getIntent().getStringExtra("Email");
        //Log.d("TEST","TEST Mail: " +email);

        restaurantRecycleView.setHasFixedSize(true);
        restaurantRecycleView.setLayoutManager(new LinearLayoutManager(this));
        //RestaurantAdapter is the class loads views
        restaurantAdapter = new RestaurantAdapter(restaurantLists, this, getEmail);
        restaurantRecycleView.setAdapter(restaurantAdapter);

        //This initializes the search restaurant page
        if (searchResults!=null) {
            for (int i = 0; i < searchResults.length; i++) {
                YelpSearchResults res = searchResults[i];
                RestaurantList restaurantList = new RestaurantList(res.getName(), res.getImage(), res.getPrice(), res.getRating(), res.getLocation(), res.getCategories());
                restaurantLists.add(restaurantList);
                restaurantAdapter.updateRestaurantList(restaurantLists);
            }
        }
        //This async task, extended runnable, is used to load the search result page
        // because there is chance the above initialization does not work
        BackThread bt = new BackThread();

        //backBtn is used to go back to main page
        backBtn.setOnClickListener(view -> {
            finish();
        });

        //refreshBtn is used when the research result does not show
        refreshBtn.setOnClickListener(view ->{
            bt.run();
        });

        //This allows user to double tab the top tool bar to quickly go back to first roll
        topBar.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {
                restaurantRecycleView.scrollToPosition(0);
            }
        });
        //This searchBtn is used to search the food user wants to eat in search bar
        searchBtn.setOnClickListener(view -> {
            restaurantAdapter.clear();
            getFood = searchField.getText().toString();
            searchResults = yelp.searchRestaurantsSync(BuildConfig.YELP_API_KEY, getFood, getLocation);
            if (searchResults!=null) {
                for (int i = 0; i < searchResults.length; i++) {
                    YelpSearchResults res = searchResults[i];
                    //Log.d("creation", i + Arrays.toString(res.getPhotos()));
                    RestaurantList restaurantList = new RestaurantList(res.getName(), res.getImage(), res.getPrice(), res.getRating(), res.getLocation(), res.getCategories());
                    restaurantLists.add(restaurantList);
                    restaurantAdapter.updateRestaurantList(restaurantLists);
                }
            }
        });

    }

    //This is a async task call to search restaurants such that it would freeze the app
    class BackThread implements Runnable {
        public void run(){
            restaurantAdapter.clear();
            getFood = searchField.getText().toString();
            searchResults = yelp.searchRestaurantsAsync(BuildConfig.YELP_API_KEY, getFood, getLocation);
            searchResults = yelp.getResults();
            if (searchResults!=null) {
                for (int i = 0; i < searchResults.length; i++) {
                    YelpSearchResults res = searchResults[i];
                    RestaurantList restaurantList = new RestaurantList(res.getName(), res.getImage(), res.getPrice(), res.getRating(), res.getLocation(), res.getCategories());
                    restaurantLists.add(restaurantList);
                    restaurantAdapter.updateRestaurantList(restaurantLists);
                }
            }
        }
    }
}