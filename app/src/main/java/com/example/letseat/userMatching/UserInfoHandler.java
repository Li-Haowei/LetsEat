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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




// This class is unusable due to async issue, will try to fix the issue to increase code reusability. We create a call back interface and function
// It somehow works, but we find that calling data within onCompleteListener is more useful.
public class UserInfoHandler {
    private String userId;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String name, number, email, Hobby, favoriteFood, dietaryRestriction, major, preferTime;
    private DocumentReference documentReference;
    private Map<String, String> map;
    private ArrayList<String> following;


    // default construct method, construct a handler for current login user
    public UserInfoHandler() {
        this.fAuth = FirebaseAuth.getInstance();
        this.fStore = FirebaseFirestore.getInstance();
        this.userId = fAuth.getCurrentUser().getUid();
        this.documentReference = fStore.collection("users").document(userId);
        this.map = new HashMap<>();
        readData(new FirestoreCallback() {
            @Override
            public void onCallback(Map<String, String> list) {
                UserInfoHandler.this.setName(list.get("fName"));
                Log.d("JJJ", "fName: " + name);
                number = list.get("phone");
                email = list.get("email");
                favoriteFood = list.get("favoriteFood");
                dietaryRestriction = list.get("dietaryRestriction");
                major = list.get("major");
                preferTime = list.get("preferTime");
                Log.d("TAG", map.toString());
            }
        });
    }

    // callback function for userInfoHandler. Read data we need here
    private void readData (FirestoreCallback firestoreCallback) {
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String number1 = document.getString("phone");
                    String name1 = document.getString("fName");
                    String email1 = document.getString("email");
                    String hobby = document.getString("hobby");
                    String favoriteFood1 = document.getString("favoriteFood");
                    String dietaryRestriction1 = document.getString("dietaryRestriction");
                    String major1 = document.getString("major");
                    String preferTime1 = document.getString("preferTime");
                    map.put("phone",number1);
                    map.put("fName", name1);
                    map.put("email", email1);
                    map.put("favoriteFood", favoriteFood1);
                    map.put("dietaryRestriction", dietaryRestriction1);
                    map.put("major", major1);
                    map.put("preferTime", preferTime1);
                    firestoreCallback.onCallback(map);
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

    // self-created callback interface
    private interface FirestoreCallback {
        void onCallback (Map<String, String> list);
    }


    // construct method, construct a handler for specific user
    public UserInfoHandler(String userId) {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        this.userId = userId;
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

    public String getID() { return this.userId; }

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

    // get user's hobby
    public String getHobby() { return Hobby; }

    // get user's prefer time
    public String getPreferTime() {
        return this.preferTime;
    }

    // get user's following list
    public ArrayList<String> getFollowing(){ return this.following; }

    public void setName (String name) {
        this.name = name;
        Log.d("TAG", "Set Name: " + name);
    }
}
