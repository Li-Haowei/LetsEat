package com.example.letseat;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.letseat.tools.ImageLoadTask;
import com.example.letseat.userMatching.Post;

import java.util.ArrayList;

public class PostPage extends AppCompatActivity {

    private TextView tvRest, tvPosting;
    private ImageView ivRest;
    private Spinner spTime;
    private ImageButton btnConfirm, btnCancel;
    private EditText etMessage;
    private RelativeLayout lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        tvRest = findViewById(R.id.tvRest);
        tvPosting = findViewById(R.id.tvPosting);
        ivRest = findViewById(R.id.ivRest);
        spTime = findViewById(R.id.spTime);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);
        etMessage = findViewById(R.id.etMessage);
        lp = findViewById(R.id.lp);
        lp.setVisibility(View.INVISIBLE);

        String restImg = getIntent().getStringExtra("restaurantImg");
        String restName = getIntent().getStringExtra("restaurantName");
        String restLocation = getIntent().getStringExtra("restaurantLocation");
        // Temp Solution: pass user email to post
        // Added by Zack
        String userEmail = getIntent().getStringExtra("userEmail");
        ArrayList<String> restLabel = getIntent().getStringArrayListExtra(("restaurantLabel"));

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
        times.add("Dinner");
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, times);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTime.setAdapter(adapter);

        new ImageLoadTask(restImg, ivRest).execute();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvRest.animate().alpha(0.0f);
                ivRest.animate().alpha(0.0f);
                spTime.setVisibility(View.INVISIBLE);
                btnConfirm.animate().alpha(0.0f);
                btnCancel.animate().alpha(0.0f);
                etMessage.animate().alpha(0.0f);
                findViewById(R.id.tvIntro).animate().alpha(0.0f);
                findViewById(R.id.tvIntro2).animate().alpha(0.0f);
                findViewById(R.id.tvRestaurant).animate().alpha(0.0f);
                findViewById(R.id.tvMessage).animate().alpha(0.0f);
                findViewById(R.id.tvTime).animate().alpha(0.0f);
                lp.setVisibility(View.VISIBLE);

                Post.makePost(restImg, restName, restLocation, userEmail, spTime.getSelectedItem().toString(), etMessage.getText().toString(), restLabel);


                Handler h2 =new Handler();
                h2.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 2000);
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
