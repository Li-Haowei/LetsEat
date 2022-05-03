package com.example.letseat.entities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.letseat.ChatActivity;
import com.example.letseat.test;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.User;
import com.example.letseat.R;
import com.example.letseat.models.request_profile;
import com.sendbird.android.log.Logger;
import com.sendbird.uikit.SendBirdUIKit;


@Layout(R.layout.adapter_request_card)
public class RequestCard  {

    // This class is the adapter for the swpie view, the object will contain all the information of
    // an invite, and the methods that define the behaviors of the swipe view

    @View(R.id.img_rest)
    private ImageView profileImageView;

    private ImageView img_rest;

    @View(R.id.tv_restName)
    private TextView tv_restName;

    @View(R.id.tv_restLabels)
    private TextView tv_restLabels;

    @View(R.id.tv_restAdd)
    private TextView tv_restAdd;

    @View(R.id.tv_InvitedBy)
    private TextView tv_InvitedBy;

    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private request_profile mProfile;

    //TODO SENDBIRD
    public RequestCard(Context context, request_profile profile , SwipePlaceHolderView swipeView) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
    }

    public request_profile getProfile() {
        return mProfile;
    }


    @Resolve
    private void onResolved(){

        Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);

        profileImageView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Log.d("EVENT", "cliecked");
                Intent intent = new Intent(mContext, test.class);
                intent.putExtra("restName",mProfile.getRestName());
                intent.putExtra("restLabels",mProfile.getRestLabels());
                intent.putExtra("restAdd",mProfile.getRestAdd());
                intent.putExtra("InvitedBy",mProfile.getInvitedBy());
                intent.putExtra("InvitedEmail", mProfile.getEmail());
                intent.putExtra("FileId", mProfile.getFileId());
                mContext.startActivity(intent);

            }
        });


        tv_restName.setText(mProfile.getRestName());
        tv_restLabels.setText(mProfile.getRestLabels());
        tv_restAdd.setText(mProfile.getRestAdd());
        tv_InvitedBy.setText(mProfile.getInvitedBy());

    }


    //if the invite is swiped left or declined, add it back to the stack of invites
    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    // if the invite is swiped right or accpeted, create a chat channel between the user and the user
    // who post the invite
    @SwipeIn
    private void onSwipeIn(){
        createChannelWithMatch(mProfile.getEmail());
        Log.d("EVENT", "onSwipedIn");

        SendBirdUIKit.connect((sb_user, e) -> {
            if (e != null) {
                return;
            }

            //If a connection is successfully built, the app will move to the mainAcitivity where the
            // chat interface is implemented
            Intent intent = new Intent(mContext, ChatActivity.class);
            mContext.startActivity(intent);

        });
        String fileId = mProfile.getFileId();
        Log.d("TEST1", "FileId: " + fileId);
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("post").document(fileId).delete();
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }

    private void createChannelWithMatch(String userId) {
        // if the invite is swiped right or accpeted, create a chat channel between the user and the user
        // who post the invite
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