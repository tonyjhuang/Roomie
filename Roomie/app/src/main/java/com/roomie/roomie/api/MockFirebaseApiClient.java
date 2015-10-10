package com.roomie.roomie.api;

import com.roomie.roomie.api.models.User;

import java.util.List;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class MockFirebaseApiClient implements FirebaseApi {

    private static MockFirebaseApiClient instance;

    private MockFirebaseApiClient() {
    }

    public static MockFirebaseApiClient getInstance() {
        if (instance == null) {
            instance = new MockFirebaseApiClient();
        }
        return instance;
    }

    @Override
    public void setCurrentUser(User user) {

    }

    @Override
    public void getCurrentUser(Callback<User> callback) {

    }

    @Override
    public void getPotentialMatches(Callback<List<User>> callback) {

    }

    @Override
    public void sendMessage(String recipientId, String message, Callback<Boolean> callback) {

    }

    @Override
    public void onReceiveMessage(String senderId, String message, Callback<Boolean> callback) {

    }
}
