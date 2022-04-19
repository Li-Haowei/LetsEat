package com.example.letseat.userMatching;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letseat.restaurant.RestaurantSearch;
import com.example.letseat.widgets.SendBirdBaseApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.log.Logger;
import com.sendbird.uikit.SendBirdUIKit;
import com.squareup.picasso.Picasso;



public class UserInfoHandler {
    private String userId;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String name, number, email, favoriteFood, dietaryRestriction, major, preferTime;


    // default construct method, construct a handler for current login user
    public UserInfoHandler() {
        this.fAuth = FirebaseAuth.getInstance();
        this.fStore = FirebaseFirestore.getInstance();
        this.userId = fAuth.getCurrentUser().getUid();
    }

    public void init() {
        final DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    number = document.getString("phone");
                    name = document.getString("fName");
                    email = document.getString("email");
                    favoriteFood = document.getString("favoriteFood");
                    dietaryRestriction = document.getString("dietaryRestriction");
                    major = document.getString("major");
                    preferTime = document.getString("preferTime");
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }
    // construct method, construct a handler for specific user
    public UserInfoHandler(String userId) {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        this.userId = userId;
    }

    public FirebaseFirestore getfStore() {
        return this.fStore;
    }

    public FirebaseAuth getfAuth() {
        return this.fAuth;
    }

    // get user's full name
    public String getName() {
        return this.name;
    }

    // get user's phone number
    public String getNumber () {
        Log.d("TAG", "getNumber: " + this.number);
        return this.number;
    }

    // get user's email
    public String getEmail() {
        return this.email;
    }

    // get user's favorite food
    public String getFavoriteFood () {
        return this.favoriteFood;
    }

    // get user's dietary restriction
    public String getDietaryRestriction() {
        return this.dietaryRestriction;
    }

    // get user's major
    public String getMajor() {
        return this.major;
    }

    // get user's prefer time
    public String getPreferTime() {
        return this.preferTime;
    }

    public void setName (String name) {
        this.name = name;
        Log.d("TAG", "Set Name: " + name);
    }
}