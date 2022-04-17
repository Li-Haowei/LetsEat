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

import com.example.letseat.utils.Utils;
import com.example.letseat.entities.RequestCard;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.sendbird.android.ApplicationUserListQuery;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBird;
import com.sendbird.android.User;
import com.sendbird.android.log.Logger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment {

    private View rootLayout;
    private Button btn_decline, btn_accept;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;



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
        mSwipeView = view.findViewById(R.id.swipeView);
        btn_decline = view.findViewById(R.id.btn_decline);
        btn_accept = view.findViewById(R.id.btn_accept);


        mContext = getActivity();

        int bottomMargin = Utils.dpToPx(100);
        Point windowSize = Utils.getDisplaySize(getActivity().getWindowManager());
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        );


        /**
         * TODO SENDBIRD
         */

        ApplicationUserListQuery query = SendBird.createApplicationUserListQuery();
        query.setLimit(100); //Whatever you want
//        query.setMetaDataFilter("dating", Collections.singletonList("True")); //Can be used if you set your own metadata on your created users.
//        query.setMetaDataFilter("sex", Collections.singletonList("female"));

        query.next((list, e) -> {
            if (e != null) {
//                Log.e(SWIPE_FRAGMENT, e.getMessage());
                return;
            }

            for (User user : list) {
                if (!user.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                    mSwipeView.addView(new RequestCard(mContext, user, mSwipeView));
                }
            }

        });


        //END

        btn_decline.setOnClickListener(v -> {
//            animateFab(fabSkip);
            mSwipeView.doSwipe(false);
        });

        btn_accept.setOnClickListener(v -> {
//            animateFab(fabLike);

            //TODO SENDBIRD IMPL
            RequestCard user = (RequestCard) mSwipeView.getAllResolvers().get(0);
            User profile = user.getUser();
            createChannelWithMatch(profile);
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