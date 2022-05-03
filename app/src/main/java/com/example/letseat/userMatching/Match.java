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

    private UserInfoHandler u1, u2;
    private boolean isMatchable;
    private double matchRate;
    private ArrayList<String> common = new ArrayList<String>();
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    public Match(String user1, String user2){
        u1 = new UserInfoHandler(user1);
        u2 = new UserInfoHandler(user2);
        isMatchable = (!user1.equals(user2));
        calcMatch();
    }

    public void calcMatch(){
        if (u1.getMajor().equals(u2.getMajor())){
            matchRate += 0.3;
            Log.d("JERRY", "You guys are both " + u1.getMajor());
            //common.add("You guys are both " + major1 + " Major");
        }
        if (u1.getFavoriteFood().equals(u1.getFavoriteFood())){
            matchRate += 0.4;
        }
    }

    public boolean getIsMatchable(){ return isMatchable; }
    public double getMatchRate(){ return matchRate; }
    public ArrayList<String> getCommon(){ return common; }

}
