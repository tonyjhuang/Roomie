package com.roomie.roomie.api;

import com.roomie.roomie.api.Callback;
import com.roomie.roomie.api.models.User;

import java.util.List;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public interface FirebaseApi {

    boolean isLoggedIn();

    void login(String facebookToken, Callback<Boolean> callback);

    void logout();

    void setCurrentUser(User user);

    void getCurrentUser(Callback<User> callback);

    void getPotentialMatches(Callback<List<User>> callback);

    void sendMessage(String recipientId, String message, Callback<Boolean> callback);

    void onReceiveMessage(String senderId, String message, Callback<Boolean> callback);
}
