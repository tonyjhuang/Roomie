package com.roomie.roomie.api;

import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.roomie.roomie.api.models.User;

import java.util.List;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class FirebaseApiClient implements FirebaseApi {

    private static final String TAG = "FIREBASE";
    private static final String FIREBASE_URL = "https://theroomieapp.firebaseio.com";

    private Firebase firebase;
    private User currentUser;
    private AuthData authData;

    private static FirebaseApiClient instance;

    private FirebaseApiClient() {
        /* Create the Firebase ref that is used for all authentication with Firebase */
        firebase = new Firebase(FIREBASE_URL);
        // Check if we have a cached auth.
        firebase.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                Log.d(TAG, "Found cached authdata.");
                setAuthData(authData);
            }
        });
    }

    public static FirebaseApiClient getInstance() {
        if (instance == null) {
            instance = new FirebaseApiClient();
        }
        return instance;
    }

    @Override
    public boolean isLoggedIn() {
        return authData != null && currentUser != null;
    }

    @Override
    public void login(String facebookToken, final Callback<Boolean> callback) {
        firebase.authWithOAuthToken("facebook", facebookToken, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                if (authData == null) {
                    Log.d(TAG, "Failed to log in, authData == null");
                    if(callback != null) {
                        callback.onResult(false);
                    }
                } else {
                    Log.d(TAG, "logged in.");
                    setAuthData(authData);
                    if(callback != null) {
                        callback.onResult(true);
                    }
                }
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Log.e(TAG, "Failed to login: " + firebaseError.getMessage());
                if(callback != null) {
                    callback.onResult(false);
                }
            }
        });
    }

    // Retrieve User information from Authdata.
    private void setAuthData(AuthData authData) {
        this.authData = authData;
        User currentUser = null;
        if(authData != null) {
            final String userId = (String) authData.getProviderData().get("id");
            currentUser = new User(userId);
            currentUser.setName((String) authData.getProviderData().get("displayName"));
            currentUser.setProfilePicture((String) authData.getProviderData().get("profileImageURL"));
            MagnetAPI.getInstance().login(userId, new Callback<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    Log.d(TAG, result ? "Logged into magnet with id: " + userId : "Couldn't login to Magnet");
                }
            });
        }
        setCurrentUser(currentUser);
    }

    @Override
    public void logout() {
        firebase.unauth();
        setAuthData(null);
    }

    @Override
    public void setCurrentUser(User user) {
        currentUser = user;
    }

    @Override
    public void getCurrentUser(Callback<User> callback) {
        callback.onResult(currentUser);
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