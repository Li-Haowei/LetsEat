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
        checkIsMatchable();
        calcMatch();
    }

    public void checkIsMatchable(){
        if (u1.getID().equals(u2.getID())){
            isMatchable = false;
        }
        else{
            isMatchable = true;
        }
    }

    public void calcMatch(){
        if (u1.getMajor().equals(u2.getMajor())){
            matchRate += 0.3;
            common.add("You guys are both " + u1.getMajor() + " Major");
        }
        else{
            common.add("Majors in " + u2.getMajor());
        }
        if (u1.getFavoriteFood().equals(u2.getFavoriteFood())){
            matchRate += 4;
            common.add("You both like " + u1.getFavoriteFood() + " food");
        }
        else{
            common.add("Likes " + u2.getFavoriteFood() + " food");
        }
        if (u1.getHobby() != null && u1.getHobby().equals(u2.getHobby())){
            matchRate += 4;
            common.add("You both like " + u1.getHobby());
        }
        else{
            common.add("Likes " + u2.getHobby());
        }
        boolean hasCommon = false;
        String line = "";
        if (u1.getFollowing() != null){
            for (int i = 0; i < u1.getFollowing().size(); i++){
                if (u2.getFollowing() != null && u2.getFollowing().contains(u1.getFollowing().get(i))) {
                    matchRate += 2;
                    if (!hasCommon){
                        line =  "You guys both followed " + u1.getFollowing();
                        hasCommon = true;
                    }
                    else{
                        line += ", " + u2.getFollowing();
                    }
                }
            }
        }
    }

    public boolean getIsMatchable(){ return isMatchable; }
    public double getMatchRate(){ return matchRate; }
    public ArrayList<String> getCommon(){ return common; }

}
