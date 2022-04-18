package com.example.letseat.entities;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.sendbird.android.User;
import com.example.letseat.R;
import com.example.letseat.models.request_profile;


@Layout(R.layout.adapter_request_card)
public class RequestCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.tv_restName)
    private TextView tv_restName;

    @View(R.id.tv_restLabels)
    private TextView tv_restLabels;

    @View(R.id.tv_restAdd)
    private TextView tv_restAdd;

    @View(R.id.tv_InvitedBy)
    private TextView tv_InvitedBy;

    private User mUser;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    private String mrestName;
    private String mrestLabels;
    private String mrestAdd;
    private String minvitedBy;
    private String murl;

    private request_profile mProfile;

    //TODO SENDBIRD
    public RequestCard(Context context, request_profile profile , SwipePlaceHolderView swipeView) {
        mContext = context;
//        mUser = user;
        mProfile = profile;

        mSwipeView = swipeView;

//        murl = url;
//        mrestName = restName;
//        mrestLabels = restLabels;
//        mrestAdd = restAdd;
//        minvitedBy = invitedBy;
    }

//    public User getUser() {
//        return mUser;
//    }

    //END

    @Resolve
    private void onResolved(){

        Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        tv_restName.setText(mProfile.getRestName());
        tv_restLabels.setText(mProfile.getRestLabels());
        tv_restAdd.setText(mProfile.getRestAdd());
        tv_InvitedBy.setText(mProfile.getInvitedBy());

    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        Log.d("EVENT", "onSwipedIn");
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }
}