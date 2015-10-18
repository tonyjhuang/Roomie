package com.roomie.roomie.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.roomie.roomie.api.Callback;
import com.roomie.roomie.api.FirebaseApi;
import com.roomie.roomie.api.FirebaseApiClient;
import com.roomie.roomie.api.models.User;

/**
 * Created by tonyjhuang on 10/15/15.
 */
public class RoomieActivity extends AppCompatActivity {
    private static final String TAG = "ROOMIEACTIVITY";
    FirebaseApi firebase = FirebaseApiClient.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkForLogin();
    }

    protected void checkForLogin() {
        // Make sure we have a logged in user.
        firebase.getCurrentUser(new Callback<User>() {
            @Override
            public void onResult(User result) {
                if (result == null) {
                    Log.e(TAG, "Not logged in, not getting cards.");
                    returnToLogin();
                }
            }
        });
    }

    protected void returnToLogin() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
