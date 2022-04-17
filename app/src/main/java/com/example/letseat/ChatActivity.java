package com.example.letseat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.uikit.fragments.ChannelListFragment;


public class ChatActivity extends AppCompatActivity {
    ImageView btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btn_back = (ImageView) findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ChannelListFragment Fragment = createChannelListFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.channelList_frag, Fragment, null)
                    .commit();
        }
    }

    protected ChannelListFragment createChannelListFragment() {

        GroupChannelListQuery query = GroupChannel.createMyGroupChannelListQuery();
        query.setIncludeEmpty(true);
        query.setOrder(GroupChannelListQuery.Order.CHRONOLOGICAL);

        return new ChannelListFragment.Builder()
                .setUseHeader(false)
                .setUseHeaderLeftButton(true)
                .setGroupChannelListQuery(query)
                .build();
    }
}

