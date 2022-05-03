package com.example.letseat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.letseat.Yelp.YelpSearchResults;
import com.example.letseat.models.request_profile;
import com.example.letseat.restaurant.RestaurantList;
import com.example.letseat.userMatching.Match;
import com.example.letseat.userMatching.Post;
import com.example.letseat.userMatching.UserInfoHandler;
import com.example.letseat.utils.Utils;
import com.example.letseat.entities.RequestCard;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.sendbird.android.ApplicationUserListQuery;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBird;
import com.sendbird.android.User;
import com.sendbird.android.log.Logger;

import java.util.ArrayList;
import java.util.Map;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link RequestFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class PostFragment extends Fragment {

    private View rootLayout;
    private Button btn_decline, btn_accept, btn_refresh;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private ArrayList<Map<String, Object>> array;
    private ImageView img_rest;

    // Matching
//    private boolean isMatchable;
//    private int matchRate = 0;
//    private ArrayList<String> common = new ArrayList<String>();
    // Personal data of current user
    private String name1, number1, email1, favoriteFood1, major1, preferTime1, hobby1;
    private ArrayList<String> following1;

    public PostFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootLayout = inflater.inflate(R.layout.fragment_request, container, false);

        return rootLayout;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeView = (SwipePlaceHolderView) view.findViewById(R.id.swipeView);
        btn_decline = (Button)view.findViewById(R.id.btn_decline);
        btn_accept = (Button)view.findViewById(R.id.btn_accept);
        btn_refresh = (Button)view.findViewById(R.id.btnSecret);
        btn_refresh.setVisibility(View.INVISIBLE);
        img_rest = (ImageView) view.findViewById(R.id.img_rest);
        BackThread bt = new BackThread();

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt.run();
            }
        });

        btn_refresh.performClick();


        mContext = getActivity();

        //setting the layout of the swipeview
        int bottomMargin = Utils.dpToPx(120);
        Point windowSize = Utils.getDisplaySize(getActivity().getWindowManager());
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(0)
                        );




        //If the decline button is clicked, it will have the same effect with swipe left
        btn_decline.setOnClickListener(v -> {
            mSwipeView.doSwipe(false);
        });

        //If the accept button is clicked, it will have the same effect with swipe right
        btn_accept.setOnClickListener(v -> {
            //equal to swipe right, accept invite
            RequestCard user = (RequestCard) mSwipeView.getAllResolvers().get(0);
            request_profile profile = user.getProfile();
            createChannelWithMatch(profile.getEmail());
            String fileId = profile.getFileId();
            Log.d("TEST1", "FileId: " + fileId);
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("post").document(fileId).delete();
            mSwipeView.doSwipe(true);
        });

    }

    private void createChannelWithMatch(String userId) {
        //This method will create a chat channel between users, the provieded string parameter is the user's id in sendbird chat server
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

    class BackThread implements Runnable {
        public void run(){
            FirebaseAuth fAuth = FirebaseAuth.getInstance();;
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            Log.d("TAG", "Getting Post");

            // Retrieve data of current user
            String currUserId = fAuth.getCurrentUser().getUid();
            DocumentReference documentReference = fStore.collection("users").document(currUserId);
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
                        hobby1 = document.getString("hobby");
                        following1 = (ArrayList<String>) document.get("followings");
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

            fStore.collection("post").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String img = document.getData().get("RestaurantImgUrl").toString();
                            String restName =  document.getData().get("RestaurantName").toString();
                            String restLocation =  document.getData().get("RestaurantLocation").toString();
                            ArrayList<String> restLabel =  (ArrayList<String>) document.getData().get("RestaurantLabel");
                            String userId =  document.getData().get("UserID").toString();
                            String email = document.getData().get("UserEmail").toString();
                            String time = document.getData().get("DinningTime").toString();
                            String message = document.getData().get("Message").toString();
                            String fileId = document.getId();

                            // Retrieve data of the poster and match him/her with current user
                            DocumentReference documentReference2 = fStore.collection("users").document(userId);
                            documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        String number2 = document.getString("phone");
                                        String name2 = document.getString("fName");
                                        String email2 = document.getString("email");
                                        String favoriteFood2 = document.getString("favoriteFood");
                                        String major2 = document.getString("major");
                                        String hobby2 = document.getString("hobby");
                                        ArrayList<String> following2 = (ArrayList<String>) document.get("followings");
                                        boolean isMatchable = (!currUserId.equals(userId)) && (time.equals(preferTime1));
                                        // Calc match rate & generate commons
                                        int matchRate = 0;
                                        ArrayList<String> common = new ArrayList<>();
                                        if (major1.equals(major2)){
                                            matchRate += 3;
                                            common.add("You both are " + major1 + " Major");
                                        }
                                        else{
                                            common.add("Major in " + major1);
                                        }
                                        if (favoriteFood1.equals(favoriteFood2)){
                                            matchRate += 4;
                                            common.add("You both like " + favoriteFood1 + " food");
                                        }
                                        else{
                                            common.add("Like " + favoriteFood1 + " food");
                                        }
                                        if (hobby1 != null && hobby1.equals(hobby2)){
                                            matchRate += 4;
                                            common.add("You both like " + hobby1);
                                        }
                                        else{
                                            common.add("Like " + hobby1);
                                        }
                                        boolean hasCommon = false;
                                        String line = "";
                                        for (int i = 0; i < following1.size(); i++){
                                            if (following2 != null && following2.contains(following1.get(i))) {
                                                matchRate += 2;
                                                if (!hasCommon){
                                                    line =  "You guys both followed " + following1;
                                                    hasCommon = true;
                                                }
                                                else{
                                                    line += ", " + following2;
                                                }
                                            }
                                        }
                                        if (hasCommon){
                                            line += " in your account";
                                            common.add(line);
                                        }
                                        else{
                                            common.add("");
                                        }

                                        if (isMatchable) {
                                            //Load the information of the invitations to the swipeviews
                                            request_profile profile = new request_profile(restName, "label", restLocation, name2, img, email, time, message, matchRate, common, fileId);
                                            mSwipeView.addView(new RequestCard(mContext, profile, mSwipeView));
                                        }
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


                        }
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }
}

