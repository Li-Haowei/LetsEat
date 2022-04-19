package com.example.letseat;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.letseat.tools.ImageLoadTask;
import com.example.letseat.userMatching.Post;

import java.util.ArrayList;

public class PostPage extends AppCompatActivity {

    private TextView tvRest, tvPost;
    private ImageView ivRest;
    private Spinner spTime;
    private Button btnConfirm, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        tvRest = findViewById(R.id.tvRest);
        tvPost = findViewById(R.id.tvPost);
        ivRest = findViewById(R.id.ivRest);
        spTime = findViewById(R.id.spTime);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);

        String restImg = getIntent().getStringExtra("resturantImg");
        String restName = getIntent().getStringExtra("resturantName");
        // Temp Solution: pass user email to post
        // Added by Zack
        String userEmail = getIntent().getStringExtra("userEmail");

        tvRest.setText(restName);

        ArrayList<String> times = new ArrayList<String>();
        // Time frames
//        times.add("8-9am");
//        times.add("9-10am");
//        times.add("10-11am");
//        times.add("11-12pm");
//        times.add("12-1pm");
//        times.add("1-2pm");
//        times.add("2-3pm");
//        times.add("3-4pm");
//        times.add("4-5pm");
//        times.add("5-6pm");
//        times.add("6-7pm");
//        times.add("7-8pm");
        // Meals
        times.add("Breakfast");
        times.add("Lunch");
        times.add("Tea");
        times.add("Dinner");
        times.add("Late Snack");
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, times);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTime.setAdapter(adapter);

        new ImageLoadTask(restImg, ivRest).execute();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post.makePost(restImg, restName, userEmail);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });





    }
}
