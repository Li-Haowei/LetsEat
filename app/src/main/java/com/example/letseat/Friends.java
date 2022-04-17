package com.example.letseat;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letseat.widgets.SendBirdBaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Friends extends AppCompatActivity {
    private RecyclerView friends;
    private FriendsAdapter friendsAdapter;
    private TextView friends_text;
    private ImageView viewProfile;
    private List<String> friendsList = new ArrayList<>();
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;
    private String userId;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        friends = findViewById(R.id.friends);
        friends_text = findViewById(R.id.friends_text);
        viewProfile = findViewById(R.id.viewProfile);

        friends.setHasFixedSize(true);
        friends.setLayoutManager(new LinearLayoutManager(this));
        friendsAdapter = new FriendsAdapter(friendsList, this);
        friends.setAdapter(friendsAdapter);

        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        DocumentReference documentReference = fStore.collection("users").document(userId);

        // retrieve friends list to be able to display
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e==null){
                    if ( documentSnapshot.exists()) {
                        friendsList = (List<String>) documentSnapshot.get("friends");
                    } else {
                        Log.d("tag", "onEvent: Document does not exist");
                    }
                }else{
                    Toast.makeText(Friends.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

}