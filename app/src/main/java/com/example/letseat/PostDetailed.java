package com.example.letseat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.letseat.tools.ImageLoadTask;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.log.Logger;
import com.sendbird.uikit.SendBirdUIKit;

import java.util.ArrayList;

public class PostDetailed extends AppCompatActivity {
    private TextView tvRestaurantName, tvRestAddress, tvName, tvMajor, tvFood, tvHobby, tvFollowing, tvTime, tvMessage;
    private ImageButton btn_back2, btn_decline2, btn_accept2;
    private ImageView imRest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detailed);

        btn_back2 = findViewById(R.id.btn_back2);
        tvRestaurantName = (TextView) findViewById(R.id.tvRestaurantName);
        tvRestAddress = (TextView) findViewById(R.id.tvRestAddress);
        tvName = (TextView) findViewById(R.id.tvName);
        tvMajor = findViewById(R.id.tvMajor);
        tvFood = findViewById(R.id.tvFood);
        tvHobby = findViewById(R.id.tvHobby);
        tvTime = findViewById(R.id.tvMeal);
        tvMessage = findViewById(R.id.tvNote);
        tvFollowing = findViewById(R.id.tvFollowing);
        btn_decline2 = findViewById(R.id.btn_decline2);
        btn_accept2 = findViewById(R.id.btn_accept2);
        imRest = findViewById(R.id.imRest);


        btn_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetailed.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Intent data = getIntent();
        String restName = data.getStringExtra("restName");
        String restLabels = data.getStringExtra("restLabels");
        String restAdd = data.getStringExtra("restAdd");
        String restImg = data.getStringExtra("restImg");
        String InvitedBy = data.getStringExtra("InvitedBy");
        String InvitedEmail = data.getStringExtra("InvitedEmail");
        String FileId = data.getStringExtra("FileId");
        String time = data.getStringExtra("time");
        String message = data.getStringExtra("message");
        int matchRate = data.getIntExtra("matchRate", 0);
        ArrayList<String> common = data.getStringArrayListExtra("common");
        new ImageLoadTask(restImg, imRest).execute();
        tvRestaurantName.setText(restName);
        tvRestAddress.setText(restAdd);
        tvTime.setText(time);
        if (message.equals("")){
            tvMessage.setText("None");
        }
        else{
            tvMessage.setText(message);
        }
        tvName.setText(InvitedBy);
        tvMajor.setText(common.get(0));
        tvFood.setText(common.get(1));
        tvHobby.setText(common.get(2));
        tvFollowing.setText(common.get(3));

        btn_decline2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetailed.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btn_accept2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendBirdUIKit.connect((sb_user, e) -> {
                    if (e != null) {
                        return;
                    }
                    createChannelWithMatch(InvitedEmail);

                    //If a connection is successfully built, the app will move to the mainAcitivity where the
                    // chat interface is implemented
                    Intent intent = new Intent(PostDetailed.this, ChatActivity.class);
                    startActivity(intent);

                });
                Log.d("TEST1", "FileId: " + FileId);
                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                fStore.collection("post").document(FileId).delete();
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