package com.example.letseat.widgets;

import android.app.Application;
import android.util.Log;


import com.example.letseat.BuildConfig;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.handlers.InitResultHandler;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.adapter.SendBirdUIKitAdapter;
import com.sendbird.uikit.interfaces.UserInfo;

public class SendBirdBaseApp extends Application {
    String App_ID = BuildConfig.SENDBIRD_API_KEY;
    String User_ID ;
    String User_Nickname ;
    @Override
    public void onCreate() {
        super.onCreate();

        //To integrate and run Sendbird UIKit with user information in our application,
        // we need to initialize an UIkit instance first.

        //According to the documentation, we can initialize an instance by passing
        // the sendBirdUIKitAdapter instance as an argument to a parameter in the
        // SendBirdUIKit.init() method.
        SendBirdUIKit.init(new SendBirdUIKitAdapter() {
            @Override
            public String getAppId() {
                return App_ID;  // Specify your Sendbird application ID.
            }

            @Override
            public String getAccessToken() {
                return "";
            }

            @Override
            public UserInfo getUserInfo() {
                return new UserInfo() {
                    @Override
                    public String getUserId() {
                        return User_ID;
                    }

                    @Override
                    public String getNickname() {
                        return User_Nickname;
                    }

                    @Override
                    public String getProfileUrl() {
                        return "";
                    }
                };
            }

            @Override
            public InitResultHandler getInitResultHandler() {
                return new InitResultHandler() {
                    @Override
                    public void onMigrationStarted() {
                        // DB migration has started.
                    }

                    @Override
                    public void onInitFailed(SendBirdException e) {
                        // If DB migration fails, this method is called.
                    }

                    @Override
                    public void onInitSucceed() {
                        // If DB migration is successful, this method is called and you can proceed to the next step.
                        // In the sample app, the `LiveData` class notifies you on the initialization progress
                        // And observes the `MutableLiveData<InitState> initState` value in `SplashActivity()`.
                        // If successful, the `LoginActivity` screen
                        // Or the `HomeActivity` screen will show.
                    }
                };
            }
        }, this);

    }

    //    Setter methods inside the BaseApplication class that will be be called in the loginActivity.
    public void setUserId(String userId) {this.User_ID = userId;}

    public void setUserNickname(String userNickname){this.User_Nickname = userNickname;}

}
