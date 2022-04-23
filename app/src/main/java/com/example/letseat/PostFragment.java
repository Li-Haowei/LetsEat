package com.example.letseat;

import android.content.Context;
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

import com.example.letseat.models.request_profile;
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
    private Button btn_decline, btn_accept;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private ArrayList<Map<String, Object>> array;

    public PostFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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


        mContext = getActivity();

        int bottomMargin = Utils.dpToPx(100);
        Point windowSize = Utils.getDisplaySize(getActivity().getWindowManager());
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(0)
                        );


        /**
         * TODO SENDBIRD
         */

//        ApplicationUserListQuery query = SendBird.createApplicationUserListQuery();
//        query.setLimit(100); //Whatever you want
////        query.setMetaDataFilter("dating", Collections.singletonList("True")); //Can be used if you set your own metadata on your created users.
////        query.setMetaDataFilter("sex", Collections.singletonList("female"));
//
//        query.next((list, e) -> {
//            if (e != null) {
////                Log.e(SWIPE_FRAGMENT, e.getMessage());
//                return;
//            }
//
//            for (User user : list) {
//                if (!user.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
//                    mSwipeView.addView(new RequestCard(mContext, user, mSwipeView));
//                }
//            }
//
//        });

        //mSwipeView.addView(new RequestCard(mContext, "1", "2", "3","4", mSwipeView));
//        for(request_profile profile : Utils.loadProfiles(getActivity().getApplicationContext())){
//            mSwipeView.addView(new RequestCard(mContext, profile, mSwipeView));
//        }
        FirebaseAuth fAuth = FirebaseAuth.getInstance();;
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        Log.d("TAG", "Getting Post");

        String currUserId = fAuth.getCurrentUser().getUid();

        fStore.collection("post").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String img = document.getData().get("ResturantImgUrl").toString();
                        String restName =  document.getData().get("ResturantName").toString();
                        String userId =  document.getData().get("UserID").toString();
                        String email = document.getData().get("UserEmail").toString();

                        // Match not working due to async.
//                        Match match = new Match(currUserId, userId);
//                        if (match.getIsMatchable()) {
//                            request_profile profile = new request_profile(restName, "label", "address", userId, img, email);
//                            mSwipeView.addView(new RequestCard(mContext, profile, mSwipeView));
//                        }

                        request_profile profile = new request_profile(restName, "label", "address",userId, img, email);
                        mSwipeView.addView(new RequestCard(mContext, profile, mSwipeView));
                        //Log.d("TAG","Array: " + array);
                        //Log.d("TAG", document.getId() + " => " + document.getData().getClass().toString());
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });



        btn_decline.setOnClickListener(v -> {
//            animateFab(fabSkip);
            mSwipeView.doSwipe(false);
        });

        btn_accept.setOnClickListener(v -> {
//            animateFab(fabLike);

            //TODO SENDBIRD IMPL
            RequestCard user = (RequestCard) mSwipeView.getAllResolvers().get(0);
            request_profile profile = user.getProfile();
            createChannelWithMatch(profile.getEmail());
            //END

            mSwipeView.doSwipe(true);
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