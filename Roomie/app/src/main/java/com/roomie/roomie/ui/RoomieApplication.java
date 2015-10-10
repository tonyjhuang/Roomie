package com.roomie.roomie.ui;

import android.app.Application;

import com.firebase.client.Firebase;
import com.magnet.mmx.client.api.MMX;
import com.roomie.roomie.R;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class RoomieApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        MMX.init(this, R.raw.roomie);
    }
}
