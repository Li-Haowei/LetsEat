package com.example.letseat.userMatching;

import com.sendbird.android.User;

import java.util.ArrayList;

public class Match {

    private UserInfoHandler u1, u2;

    private boolean isMatchable;
    private double matchRate;
    private ArrayList<String> common;

    public Match(String user1, String user2){
        u1 = new UserInfoHandler(user1);
        u2 = new UserInfoHandler(user2);
        isMatchable = (u1.getPreferTime() == u2.getPreferTime());
        calcMatch();
    }

    public void calcMatch(){
        if (u1.getMajor() == u2.getMajor()){
            matchRate += 0.3;
            common.add("You guys are both " + u1.getMajor() + " Major");
        }
        if (u1.getFavoriteFood() == u2.getFavoriteFood()){
            matchRate += 0.4;
        }
    }

    public boolean getIsMatchable(){ return isMatchable; }
    public double getMatchRate(){ return matchRate; }
    public ArrayList<String> getCommon(){ return common; }

}
