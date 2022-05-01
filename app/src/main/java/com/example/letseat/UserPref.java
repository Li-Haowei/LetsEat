package com.example.letseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letseat.optionsPage.FavoriteFoodCuisine_Register;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UserPref extends AppCompatActivity {
    private String fullName, email, phone, food, user_major, time, hobby;
    private Spinner preferTime, major, hobbies;
    private Button saveBtn;
    private TextView favoriteFood;
    private ImageView profileImageView;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;
    private StorageReference storageReference;
    private final float textSize = 30.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pref);

        Intent data = getIntent();
        fullName = data.getStringExtra("fullName");
        email = data.getStringExtra("email");
        phone = data.getStringExtra("phone");
        food = data.getStringExtra("favoriteFood");
        user_major = data.getStringExtra("major");
        time = data.getStringExtra("preferTime");
        hobby = data.getStringExtra("hobby");
        /*
        FirebaseAuth is what we used to sign in users to our Firebase app
        https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth
         */
        fAuth = FirebaseAuth.getInstance();
        /*
        Firestore database and is the entry point for all Cloud Firestore operations.
        https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/FirebaseFirestore#:~:text=Inherited%20Method%20Summary-,Public%20Methods,for%20all%20Cloud%20Firestore%20operations.
         */
        fStore = FirebaseFirestore.getInstance();
        /*
        get user
         */
        user = fAuth.getCurrentUser();
        /*
        Creates a new StorageReference initialized at the root Firebase Storage location.
        More details: https://firebase.google.com/docs/reference/android/com/google/firebase/storage/FirebaseStorage#getReference()
         */
        storageReference = FirebaseStorage.getInstance().getReference();

        /*
        Bind fields to views
         */
        favoriteFood = new TextView(this);

        // create a list of items for the major spinner
        major = new Spinner(this);
        String[] items_major = new String[]{"Acting", "Advertising","Anthropology", "Art", "Astronomy", "Biology", "Business", "Chemistry", "Computer Engineering", "Computer Science", "Film/TV", "History", "International Relations", "Journalism", "Math", "Neuroscience", "Philosophy", "Political Science", "Religion"};
        ArrayAdapter<String> adapter_majors = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_major);
        major.setAdapter(adapter_majors);
        // to be able to display user specific preferences for each account
        for (int i = 0; i < items_major.length; i++) {
            if(items_major[i].equals(user_major)){
                Log.d("creation", items_major[i]);
                major.setSelection(i);
                break;
            }
        }
        // listener to save user-selected spinner value
        major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                user_major = items_major[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // create a list of items for the preferred time spinner
        preferTime = new Spinner(this);
        String[] items_time = new String[]{"Breakfast", "Lunch", "Dinner"};
        ArrayAdapter<String> adapter_time = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_time);
        preferTime.setAdapter(adapter_time);
        // to be able to display user specific preferences for each account
        for (int i = 0; i < items_time.length; i++) {
            if(items_time[i].equals(time)){
                preferTime.setSelection(i);
                break;
            }
        }
        // listener to save user-selected spinner value
        preferTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                time = items_time[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // create a list of items for the hobby spinner
        hobbies = new Spinner(this);
        String[] items_hobby = new String[]{"Sport", "Car", "Gaming", "Photography", "Anime", "Movies", "Travel", "Programming"};
        ArrayAdapter<String> adapter_hobby = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_hobby);
        hobbies.setAdapter(adapter_hobby);
        // to be able to display user specific preferences for each account
        for (int i = 0; i < items_hobby.length; i++) {
            if(items_hobby[i].equals(hobby)){
                hobbies.setSelection(i);
                break;
            }
        }
        // listener to save user-selected spinner value
        hobbies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                hobby= items_hobby[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        saveBtn = new Button(this);
        saveBtn.setText("SAVE");
        saveBtn.setHeight(30);
        saveBtn.setWidth(60);

        // Programmatically creating views for name, phone, email, and etc.
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

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.setMargins(20,0,20, 40);


        //Food section
        TextView tv3 = new TextView(this);
        tv3.setText(R.string.user_food);
        tv3.setTypeface(null, Typeface.BOLD);
        tv3.setTextColor(getResources().getColor(R.color.white));
        tv3.setLayoutParams(params1);
        linearLayout.addView(tv3);
        linearLayout.addView(favoriteFood);
        favoriteFood.setBackground(getResources().getDrawable(R.drawable.round_back_white_10));
        favoriteFood.setText(food);
        favoriteFood.setTextSize(textSize);
        favoriteFood.setTextColor(getResources().getColor(R.color.black));
        favoriteFood.setLayoutParams(params);

        //Major section
        TextView tv4 = new TextView(this);
        tv4.setText(R.string.user_major);
        tv4.setTypeface(null, Typeface.BOLD);
        tv4.setTextColor(getResources().getColor(R.color.white));
        tv4.setLayoutParams(params1);
        linearLayout.addView(tv4);
        linearLayout.addView(major);
        major.setBackground(getResources().getDrawable(R.drawable.round_back_white_10));
        major.setLayoutParams(params);

        //Time section
        TextView tv5 = new TextView(this);
        tv5.setText(R.string.user_pref_time);
        tv5.setTypeface(null, Typeface.BOLD);
        tv5.setTextColor(getResources().getColor(R.color.white));
        tv5.setLayoutParams(params1);
        linearLayout.addView(tv5);
        linearLayout.addView(preferTime);
        preferTime.setBackground(getResources().getDrawable(R.drawable.round_back_white_10));
        preferTime.setLayoutParams(params);

        //Hobby section
        TextView tv6 = new TextView(this);
        tv6.setText(R.string.user_hobby);
        tv6.setTypeface(null, Typeface.BOLD);
        tv6.setTextColor(getResources().getColor(R.color.white));
        tv6.setLayoutParams(params1);
        linearLayout.addView(tv6);
        linearLayout.addView(hobbies);
        hobbies.setBackground(getResources().getDrawable(R.drawable.round_back_white_10));
        hobbies.setLayoutParams(params);

        linearLayout.addView(saveBtn);

        //add scrollable view into rootContainer
        LinearLayout linear = findViewById(R.id.rootContainer);
        linear.addView(scrollView);

        profileImageView = findViewById(R.id.img_rest);

        saveBtn.setOnClickListener(view -> {
            if(favoriteFood.getText().toString().isEmpty() || major.getSelectedItem() == null
                    || preferTime.getSelectedItem() == null) {
                Log.d("PREFERTIME", preferTime.getSelectedItem().toString());
                Log.d("MAJOR", major.getSelectedItem().toString());
                Log.d("FAVFOOD", favoriteFood.getText().toString());
                Toast.makeText(UserPref.this, "one or many fields are empty", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(UserPref.this, "Loading", Toast.LENGTH_SHORT).show();
            user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    DocumentReference docRef = fStore.collection("users").document(user.getUid());
                    Map<String,Object> edited = new HashMap<>();
                    edited.put("favoriteFood",(Object) food);
                    edited.put("major",(Object) user_major);
                    edited.put("preferTime",(Object) time);
                    edited.put("hobby",(Object) hobby);
                    docRef.update(edited);
                    Toast.makeText(UserPref.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserPref.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        /*
        StorageReference represents a reference to a Google Cloud Storage object.
        Developers can upload and download objects, get/set object metadata,
        and delete an object at a specified path.

        More detail please refer to example illustration in Register.java
         */
        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });


        // to have the correct user-specific information displayed when switching intents
        favoriteFood.setOnClickListener(view -> {
            Intent foodOptionsIntent = new Intent(view.getContext(), FavoriteFoodCuisine_Register.class);
            foodOptionsIntent.putExtra("email", email);
            foodOptionsIntent.putExtra("favoriteFood", food);
            foodOptionsIntent.putExtra("major", user_major);
            foodOptionsIntent.putExtra("preferTime", time);
            startActivity(foodOptionsIntent);
            finish();
        });

    }
}