package com.example.letseat.userMatching;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sendbird.android.User;

import java.util.ArrayList;

// This class is unusable due to async issue, will try to fix the issue to increase code reusability.
public class Match {

    //private UserInfoHandler u1, u2;
    private boolean isMatchable;
    private double matchRate;
    private ArrayList<String> common = new ArrayList<String>();
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String name1, number1, email1, favoriteFood1, major1, preferTime1;
    private String name2, number2, email2, favoriteFood2, major2, preferTime2;

    public Match(String user1, String user2){
//        u1 = new UserInfoHandler(user1);
//        u2 = new UserInfoHandler(user2);
        this.fAuth = FirebaseAuth.getInstance();
        this.fStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fStore.collection("users").document(user1);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    number1 = document.getString("phone");
                    name1 = document.getString("fName");
                    email1 = document.getString("email");
                    favoriteFood1 = document.getString("favoriteFood");
                    major1 = document.getString("major");
                    preferTime1 = document.getString("preferTime");
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

        documentReference = fStore.collection("users").document(user2);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    number2 = document.getString("phone");
                    name2 = document.getString("fName");
                    email2 = document.getString("email");
                    favoriteFood2 = document.getString("favoriteFood");
                    major2 = document.getString("major");
                    preferTime2 = document.getString("preferTime");
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
//                isMatchable = ((preferTime1.equals(preferTime2)) && (!user1.equals(user2)));
            }
        });
        isMatchable = (!user1.equals(user2));
        calcMatch();
    }

    public void calcMatch(){
        if (major1.equals(major2)){
            matchRate += 0.3;
            Log.d("JERRY", "You guys are both " + major1);
            //common.add("You guys are both " + major1 + " Major");
        }
        if (favoriteFood1.equals(favoriteFood2)){
            matchRate += 0.4;
        }
    }

    public boolean getIsMatchable(){ return isMatchable; }
    public double getMatchRate(){ return matchRate; }
    public ArrayList<String> getCommon(){ return common; }

}
