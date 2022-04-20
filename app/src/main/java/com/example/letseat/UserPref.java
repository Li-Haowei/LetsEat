package com.example.letseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letseat.optionsPage.FavoriteFoodCuisine;
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
    private EditText favoriteFood, dietaryRestriction, major, preferTime;
    private String name, email, phone;
    private Button saveBtn;
    private ImageView profileImageView;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pref);

        Intent data = getIntent();
        name = data.getStringExtra("fullName");
        email = data.getStringExtra("email");
        phone = data.getStringExtra("phone");
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

        favoriteFood = new EditText(this);
        major = new EditText(this);
        dietaryRestriction = new EditText(this);
        preferTime = new EditText(this);
        saveBtn = new Button(this);
        saveBtn.setText("SAVE");
        saveBtn.setHeight(30);
        saveBtn.setWidth(60);
        /*
        Intent foodOptionsIntent = new Intent(view.getContext(), FavoriteFoodCuisine.class);
        foodOptionsIntent.putExtra("fullName", fullName );
        foodOptionsIntent.putExtra("email", email);
        foodOptionsIntent.putExtra("phone", phone);
        foodOptionsIntent.putExtra("favoriteFood", food);
        foodOptionsIntent.putExtra("major", user_major);
        foodOptionsIntent.putExtra("preferTime", time);
        startActivity(foodOptionsIntent);

         */


        EditText[] editTextManager = {favoriteFood, major, dietaryRestriction, preferTime};
        for (int i = 0; i < editTextManager.length; i++) {
            editTextManager[i].setSingleLine();
            editTextManager[i].setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(layoutParams);
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(linearParams);
        scrollView.addView(linearLayout);


        TextView tv3 = new TextView(this);
        tv3.setText("Favorite Food");
        linearLayout.addView(tv3);
        linearLayout.addView(favoriteFood);
        TextView tv4 = new TextView(this);
        tv4.setText("Major");
        linearLayout.addView(tv4);
        linearLayout.addView(major);
        TextView tv5 = new TextView(this);
        tv5.setText("Dietary Restriction");
        linearLayout.addView(tv5);
        linearLayout.addView(dietaryRestriction);
        TextView tv6 = new TextView(this);
        tv6.setText("Prefer Time to Eat");
        linearLayout.addView(tv6);
        linearLayout.addView(preferTime);

        linearLayout.addView(saveBtn);

        LinearLayout linear = findViewById(R.id.rootContainer);
        linear.addView(scrollView);

        profileImageView = findViewById(R.id.profileImageView);

        saveBtn.setOnClickListener(view -> {
            if(favoriteFood.getText().toString().isEmpty() || major.getText().toString().isEmpty()
                    || dietaryRestriction.getText().toString().isEmpty() || preferTime.getText().toString().isEmpty()){
                Toast.makeText(UserPref.this, "one or many field are empty", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(UserPref.this, "Loading", Toast.LENGTH_SHORT).show();
            user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    DocumentReference docRef = fStore.collection("users").document(user.getUid());
                    Map<String,Object> edited = new HashMap<>();
                    edited.put("favoriteFood",favoriteFood.getText().toString());
                    edited.put("major",major.getText().toString());
                    edited.put("dietaryRestriction",dietaryRestriction.getText().toString());
                    edited.put("preferTime",preferTime.getText().toString());
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
    }
}