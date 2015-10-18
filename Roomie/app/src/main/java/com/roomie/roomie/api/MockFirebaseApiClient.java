package com.roomie.roomie.api;

import com.google.android.gms.maps.model.LatLng;
import com.roomie.roomie.api.models.Location;
import com.roomie.roomie.api.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class MockFirebaseApiClient implements FirebaseApi {

    public static List<User> mockUsers = new ArrayList<>();
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
    public void resetData(){}

    @Override
    public String getCurrentUserId(){
        return "";
    }
    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public void login(String facebookToken, Callback<Boolean> callback) {
        callback.onResult(true);
    }

    @Override
    public void logout() {
        // No-op.
    }

    @Override
    public void setCurrentUser(User user) {
        // No-op.
    }

    @Override
    public void getCurrentUser(Callback<User> callback) {
        callback.onResult(mockUsers.get(0));
    }

    @Override
    public void getPotentialMatches(LatLng latLng, Callback<List<String>> callback) {
        ArrayList<String> ids = new ArrayList<>();
        for (User mock : mockUsers) {
            ids.add(mock.getId());
        }
        callback.onResult(ids);
    }

    @Override
    public void sendMessage(String recipientId, String message, Callback<Boolean> callback) {

    }

    @Override
    public void onReceiveMessage(String senderId, String message, Callback<Boolean> callback) {

    }
}
