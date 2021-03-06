package com.example.letseat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letseat.restaurant.RestaurantSearch;
import com.example.letseat.userMatching.UserInfoHandler;
import com.example.letseat.widgets.SendBirdBaseApp;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


/*
This is the main page where all the magic happen. For deeper understanding of user class and how they work
in firebase, please go through the following classes as listed in order: Register.java, Login.java, EditProfile.java.

This main page intends to contain all the links to other pages, for convenient and cool designs, I removed default action
and tasks bar, the action bar we have now is a customizable relative layout
 */
public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_INTENT_CODE = 1023 ;
    private String name, number, email, favoriteFood, dietaryRestriction, major, preferTime, hobby;
    private LinearLayout pleaseVerify;
    private TextView titleMain;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;
    private Button resendCode ;
    private ImageView changeProfile, searchRestaurant, btn_chatList;
    private FirebaseUser user;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeProfile = findViewById(R.id.changeProfile);
        searchRestaurant = findViewById(R.id.search_Restaurant);
        /*
        FirebaseAuth is what we used to sign in users to our Firebase app.
        Firestore database and is the entry point for all Cloud Firestore operations.
        */
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        /*
        StorageReference represents a reference to a Google Cloud Storage object.
        Developers can upload and download objects, get/set object metadata,
        and delete an object at a specified path.

        More detail please refer to example illustration in Register.java
         */
        storageReference = FirebaseStorage.getInstance().getReference();

        btn_chatList = (ImageView) findViewById(R.id.btn_chatList);

        resendCode = findViewById(R.id.resendCode);
        titleMain = findViewById(R.id.titleMain);
        pleaseVerify = findViewById(R.id.pleaseVerify);

        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        // Placing the post fragment in the main activity
        getSupportFragmentManager().beginTransaction().replace(R.id.request_fragment_in_main, new PostFragment(), "RequestFragment").commit();

        if (!user.isEmailVerified()) {
            pleaseVerify.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(view.getContext(), "Verification email sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "onFailure:email not sent" + e.getMessage());
                        }
                    });
                }
            });
        }
        /*
        Here we use DocumentReference to achieve data from our server, and set our views based on the data
        document snapshot can be understand as the response from our server based on our request,
        and in this case, it is users.userID, more details please refer to Register.java "example illustration"
         */
        DocumentReference documentReference = fStore.collection("users").document(userId);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e==null){
                    if ( documentSnapshot.exists()) {
                        number = documentSnapshot.getString("phone");
                        name = documentSnapshot.getString("fName");
                        email = documentSnapshot.getString("email");
                        favoriteFood = documentSnapshot.getString("favoriteFood");
                        dietaryRestriction = documentSnapshot.getString("dietaryRestriction");
                        major = documentSnapshot.getString("major");
                        preferTime = documentSnapshot.getString("preferTime");
                        hobby = documentSnapshot.getString("hobby");

                        // Registering to the sendbird chat server with user's information with the methods defined in the base application
                        // If user is already registered in sendbird server, application will log in user to sendbird chat server
                        ((SendBirdBaseApp)getApplication()).setUserId(documentSnapshot.getString("email"));
                        ((SendBirdBaseApp)getApplication()).setUserNickname(documentSnapshot.getString("fName"));

                        SendBirdUIKit.connect((sb_user, except) -> {
                            if (except != null) {
                                return;
                            }
                        });


                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });
        UserInfoHandler handler = new UserInfoHandler();
        Log.d("TEST", "FName: " + handler.getName());

        //changeProfile is a button that goes to event EditProfile.java
        // where users make changes to their personal information
        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), EditProfile.class);
                i.putExtra("fullName", name);
                i.putExtra("email", email);
                i.putExtra("phone", number);
                i.putExtra("favoriteFood", favoriteFood);
                i.putExtra("dietaryRestriction", dietaryRestriction);
                i.putExtra("major", major);
                i.putExtra("preferTime", preferTime);
                i.putExtra("hobby", hobby);
                startActivity(i);
            }
        });
        //searchRestaurant is a button that goes to event RestaurantSearch
        // where users can search restaurants to make a post to others
        searchRestaurant.setOnClickListener(view ->{
            Toast.makeText(this,"Restaurants loading",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(view.getContext(), RestaurantSearch.class);
            i.putExtra("food", favoriteFood);
            i.putExtra("location","Boston");
            // Temp Solution: pass user email to post
            // Added by Zack
            i.putExtra("email", email);
            startActivity(i);
        });
        //This serves the same purpose as searchRestaurant
        titleMain.setOnClickListener(view ->{
            Toast.makeText(this,"Restaurants loading",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(view.getContext(), RestaurantSearch.class);
            i.putExtra("food", favoriteFood);
            i.putExtra("location","Boston");
            // Temp Solution: pass user email to post
            // Added by Zack
            i.putExtra("email", email);
            startActivity(i);
        });
        //This goes to ChatActivity
        btn_chatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendBirdUIKit.connect((sb_user, e) -> {
                    if (e != null) {
                        return;
                    }

                    //If a connection is successfully built, the app will move to the mainAcitivity where the
                    // chat interface is implemented
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent);

                });

            }
        });
    }






}