package com.example.letseat;

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
import com.squareup.picasso.Picasso;


/*
This is the main page where all the magic happen. For deeper understanding of user class and how they work
in firebase, please go through the following classes as listed in order: Register.java, Login.java, EditProfile.java.

This main page intends to contain all the links to other pages, for convenient and cool designs, I removed default action
and tasks bar, the action bar we have now is a customizable relative layout
 */
public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_INTENT_CODE = 1023 ;
    private String name, number, email, favoriteFood, dietaryRestriction, major, preferTime;
    private TextView verifyMsg;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;
    private Button resendCode;
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
        verifyMsg = findViewById(R.id.verifyMsg);

        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        if (!user.isEmailVerified()) {
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);

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
        Log.d("creation", userId);
        Log.d("creation", fStore.collection("users").toString());

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

                        ((SendBirdBaseApp)getApplication()).setUserId(documentSnapshot.getString("email"));
                        ((SendBirdBaseApp)getApplication()).setUserNickname(documentSnapshot.getString("fName"));
                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });
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
                startActivity(i);
            }
        });
        searchRestaurant.setOnClickListener(view ->{
            Intent i = new Intent(view.getContext(), RestaurantSearch.class);
            i.putExtra("food", favoriteFood);
            i.putExtra("location","Boston");
            startActivity(i);
        });

        btn_chatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendBirdUIKit.connect((sb_user, e) -> {
                    if (e != null) {
                        return;
                    }


                    createChannelWithMatch("Wanzhi");
                    //If a connection is successfully built, the app will move to the mainAcitivity where the
                    // chat interface is implemented
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent);
//                    Toast.makeText(MainActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();

                });

            }
        });

    }

    private void createChannelWithMatch(String userId) {
        GroupChannelParams params = new GroupChannelParams();
        params.setDistinct(true)
                .addUserId(userId);

        GroupChannel.createChannel(params, (groupChannel, e) -> {
            if (e != null) {
                Logger.e(e.getMessage());
                return;
            }
            Logger.d(groupChannel.getUrl() + ": Channel Created");

        });
    }

}