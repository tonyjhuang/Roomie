package com.roomie.roomie.ui;

import android.app.Application;
import android.content.Context;

import com.firebase.client.Firebase;
import com.magnet.mmx.client.api.MMX;
import com.roomie.roomie.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }
}
