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
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.log.Logger;
import com.sendbird.uikit.SendBirdUIKit;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Post {

    private FirebaseFirestore fStore;
    private ArrayList<Map<String, Object>> array;

    /*
    * This function generates a post including poster and the restaurant info. The post will
    * be stored in the database for other users to receive and choose whether to accept or not.
    * */
    public static void makePost(String restaurantImgUrl, String restaurantName, String userEmail, String time, String message){
        FirebaseAuth fAuth = FirebaseAuth.getInstance();;
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        String userId = fAuth.getCurrentUser().getUid();

        Map<String, Object> post = new HashMap<>();
        post.put("UserID", userId);
        post.put("UserEmail", userEmail);
        post.put("ResturantName", restaurantName);
        post.put("ResturantImgUrl", restaurantImgUrl);
        post.put("DinningTime", time);
        post.put("Message", message);
        fStore.collection("post").add(post).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.d("Post", "OnSuccess: a new post being created" + userId);
            }
        });
    }

    @NonNull
    public ArrayList<Map<String, Object>> getPost () {
        this.array = new ArrayList<>();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();;
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        Log.d("TAG", "Getting Post");
        fStore.collection("post").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        array.add(document.getData());
                        //Log.d("TAG","Array: " + array);
                        //Log.d("TAG", document.getId() + " => " + document.getData().getClass().toString());
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

        readData(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Map<String, Object>> array1) {
                array = array1;
                Log.d("TAG", "Callback: " + array);
            }
        });
        return array;
    }
    private interface FirestoreCallback {
        void onCallback (ArrayList<Map<String, Object>> array);
    }

    private void readData (FirestoreCallback firestoreCallback) {
        fStore.collection("post").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        array.add(document.getData());
                        //Log.d("TAG","Array: " + array);
                        //Log.d("TAG", document.getId() + " => " + document.getData().getClass().toString());
                    }
                    firestoreCallback.onCallback(array);
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
