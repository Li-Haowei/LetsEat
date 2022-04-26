package com.example.letseat.optionsPage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.letseat.UserPref;
import com.example.letseat.R;

public class FavoriteFoodCuisine_Register extends AppCompatActivity {
    private final float textSize = 20.0f;
    private String fullName, email, food, phone, user_major, time;
    private ImageView backBtn;
    private LinearLayout linear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_food_cuisine);
        backBtn = findViewById(R.id.backBtn);
        linear = findViewById(R.id.rootContainer);

        Intent data = getIntent();
        fullName = data.getStringExtra("fullName");
        email = data.getStringExtra("email");
        phone = data.getStringExtra("phone");
        food = data.getStringExtra("favoriteFood");
        user_major = data.getStringExtra("major");
        time = data.getStringExtra("preferTime");

        //Programmatically creating views for name, phone, email, and etc.
        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(layoutParams);
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(linearParams);
        scrollView.addView(linearLayout);


        //Set layout parameters for the input field
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20,0,20, 40);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.setMargins(20,0,20, 0);

        //American section
        TextView tv0 = new TextView(this);
        tv0.setText(R.string.American);
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setTextColor(getResources().getColor(R.color.white));
        tv0.setLayoutParams(params1);
        linearLayout.addView(tv0);
        ImageView imgv0 = new ImageView(this);
        imgv0.setBackgroundResource(R.drawable.americanfood);
        linearLayout.addView(imgv0);

        //Korean section
        TextView tv1 = new TextView(this);
        tv1.setText(R.string.Korean);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setTextColor(getResources().getColor(R.color.white));
        tv1.setLayoutParams(params1);
        linearLayout.addView(tv1);
        ImageView imgv1 = new ImageView(this);
        imgv1.setBackgroundResource(R.drawable.koreanfood);
        linearLayout.addView(imgv1);

        //Chinese section
        TextView tv2 = new TextView(this);
        tv2.setText(R.string.Chinese);
        tv2.setTypeface(null, Typeface.BOLD);
        tv2.setTextColor(getResources().getColor(R.color.white));
        tv2.setLayoutParams(params1);
        linearLayout.addView(tv2);
        ImageView imgv2 = new ImageView(this);
        imgv2.setBackgroundResource(R.drawable.chinesefood);
        linearLayout.addView(imgv2);


        //Thai section
        TextView tv3 = new TextView(this);
        tv3.setText(R.string.Thai);
        tv3.setTypeface(null, Typeface.BOLD);
        tv3.setTextColor(getResources().getColor(R.color.white));
        tv3.setLayoutParams(params1);
        linearLayout.addView(tv3);
        ImageView imgv3 = new ImageView(this);
        imgv3.setBackgroundResource(R.drawable.thaifood);
        linearLayout.addView(imgv3);


        //Japanese section
        TextView tv4 = new TextView(this);
        tv4.setText(R.string.Japanese);
        tv4.setTypeface(null, Typeface.BOLD);
        tv4.setTextColor(getResources().getColor(R.color.white));
        tv4.setLayoutParams(params1);
        linearLayout.addView(tv4);
        ImageView imgv4 = new ImageView(this);
        imgv4.setBackgroundResource(R.drawable.japanesefood);
        linearLayout.addView(imgv4);


        //Italian section
        TextView tv5 = new TextView(this);
        tv5.setText(R.string.Italian);
        tv5.setTypeface(null, Typeface.BOLD);
        tv5.setTextColor(getResources().getColor(R.color.white));
        tv5.setLayoutParams(params1);
        linearLayout.addView(tv5);
        ImageView imgv5 = new ImageView(this);
        imgv5.setBackgroundResource(R.drawable.italianfood);
        linearLayout.addView(imgv5);

        //French section
        TextView tv6 = new TextView(this);
        tv6.setText(R.string.French);
        tv6.setTypeface(null, Typeface.BOLD);
        tv6.setTextColor(getResources().getColor(R.color.white));
        tv6.setLayoutParams(params1);
        linearLayout.addView(tv6);
        ImageView imgv6 = new ImageView(this);
        imgv6.setBackgroundResource(R.drawable.frenchfood);
        linearLayout.addView(imgv6);

        ImageView[] allImageViews = {imgv0,imgv1,imgv2,imgv3,imgv4,imgv5,imgv6};
        TextView[] allTextViews = {tv0,tv1,tv2,tv3,tv4,tv5,tv6};

        for (int i = 0; i < allImageViews.length; i++) {
            allTextViews[i].setGravity(Gravity.CENTER);
            allTextViews[i].setTextSize(textSize);
            setImageViewOnclickListener(allTextViews[i].getText().toString(), allImageViews[i]);
        }

        //add scrollable view into rootContainer
        linear.addView(scrollView);

        backBtn.setOnClickListener(view -> {
            finish();
        });
    }
    private void setImageViewOnclickListener(String foodType, ImageView imgv){
        imgv.setOnClickListener(view -> {
            Intent i = new Intent(view.getContext(), UserPref.class);
            i.putExtra("email", email);
            i.putExtra("favoriteFood", foodType);
            i.putExtra("major", user_major);
            i.putExtra("preferTime", time);
            startActivity(i);
            finish();
        });
    }
}