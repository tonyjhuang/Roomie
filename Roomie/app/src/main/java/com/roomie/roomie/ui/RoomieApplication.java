package com.roomie.roomie.ui;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class RoomieApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
