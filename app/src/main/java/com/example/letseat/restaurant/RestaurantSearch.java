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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    private final List<RestaurantList> restaurantLists = new ArrayList<>();
    private RestaurantAdapter restaurantAdapter;
    private RecyclerView restaurantRecycleView;
    private YelpSearchResults[] searchResults;
    private RelativeLayout topBar;
    private Yelp yelp = new Yelp("https://api.yelp.com/v3/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);

        backBtn = findViewById(R.id.backBtn);
        searchBtn = findViewById(R.id.searchBtn);
        refreshBtn = findViewById(R.id.refreshBtn);
        restaurantRecycleView = findViewById(R.id.restaurantRecyclerView);
        topBar = findViewById(R.id.topBar);

        final String getFood = getIntent().getStringExtra("food");
        final String getLocation = getIntent().getStringExtra("location");
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
            /*
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.foodOptions, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setVisibility(View.VISIBLE);

             */
        });

    }
    class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
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