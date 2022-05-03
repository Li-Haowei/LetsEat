package com.example.letseat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.log.Logger;
import com.sendbird.uikit.SendBirdUIKit;

public class test extends AppCompatActivity {
    private TextView info1, info2, info3, info4;
    private Button btn_back2, btn_decline2, btn_accept2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btn_back2 = (Button) findViewById(R.id.btn_back2);
        info1 = (TextView) findViewById(R.id.info1);
        info2 = (TextView) findViewById(R.id.info2);
        info3 = (TextView) findViewById(R.id.info3);
        info4 = (TextView) findViewById(R.id.info4);
        btn_decline2 = (Button)findViewById(R.id.btn_decline2);
        btn_accept2 = (Button)findViewById(R.id.btn_accept2);


        btn_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(test.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Intent data = getIntent();
        String restName = data.getStringExtra("restName");
        String restLabels = data.getStringExtra("restLabels");
        String restAdd = data.getStringExtra("restAdd");
        String InvitedBy = data.getStringExtra("InvitedBy");
        String InvitedEmail = data.getStringExtra("InvitedEmail");
        info1.setText(restName);
        info2.setText(restLabels);
        info3.setText(restAdd);
        info4.setText(InvitedBy);

        btn_decline2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(test.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btn_accept2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createChannelWithMatch(InvitedEmail);

                SendBirdUIKit.connect((sb_user, e) -> {
                    if (e != null) {
                        return;
                    }

                    //If a connection is successfully built, the app will move to the mainAcitivity where the
                    // chat interface is implemented
                    Intent intent = new Intent(test.this, ChatActivity.class);
                    startActivity(intent);

                });

            }
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
}