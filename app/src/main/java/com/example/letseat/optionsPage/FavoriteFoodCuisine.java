package com.example.letseat.optionsPage;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.letseat.R;

public class FavoriteFoodCuisine extends AppCompatActivity {
    private ImageView backBtn;
    private LinearLayout linear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_food_cuisine);

        backBtn = findViewById(R.id.backBtn);
        linear = findViewById(R.id.rootContainer);

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

        //Korean section
        TextView tv1 = new TextView(this);
        tv1.setText(R.string.Korean);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setTextColor(getResources().getColor(R.color.white));
        tv1.setLayoutParams(params1);
        linearLayout.addView(tv1);

        //Chinese section
        TextView tv2 = new TextView(this);
        tv2.setText(R.string.Chinese);
        tv2.setTypeface(null, Typeface.BOLD);
        tv2.setTextColor(getResources().getColor(R.color.white));
        tv2.setLayoutParams(params1);
        linearLayout.addView(tv2);


        //Thai section
        TextView tv3 = new TextView(this);
        tv3.setText(R.string.Thai);
        tv3.setTypeface(null, Typeface.BOLD);
        tv3.setTextColor(getResources().getColor(R.color.white));
        tv3.setLayoutParams(params1);
        linearLayout.addView(tv3);


        //Japanese section
        TextView tv4 = new TextView(this);
        tv4.setText(R.string.Japanese);
        tv4.setTypeface(null, Typeface.BOLD);
        tv4.setTextColor(getResources().getColor(R.color.white));
        tv4.setLayoutParams(params1);
        linearLayout.addView(tv4);


        //Italian section
        TextView tv5 = new TextView(this);
        tv5.setText(R.string.Italian);
        tv5.setTypeface(null, Typeface.BOLD);
        tv5.setTextColor(getResources().getColor(R.color.white));
        tv5.setLayoutParams(params1);
        linearLayout.addView(tv5);

        //French section
        TextView tv6 = new TextView(this);
        tv6.setText(R.string.French);
        tv6.setTypeface(null, Typeface.BOLD);
        tv6.setTextColor(getResources().getColor(R.color.white));
        tv6.setLayoutParams(params1);
        linearLayout.addView(tv6);

        //add scrollable view into rootContainer
        linear.addView(scrollView);
    }
}