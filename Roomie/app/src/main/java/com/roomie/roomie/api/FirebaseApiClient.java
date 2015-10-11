package com.roomie.roomie.api;

import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.roomie.roomie.api.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class FirebaseApiClient implements FirebaseApi {

    private static final String TAG = "FIREBASE";
    private static final String FIREBASE_URL = "https://theroomieapp.firebaseio.com";

    public static Firebase firebase = new Firebase(FIREBASE_URL);
    public static GeoFire geofire = new GeoFire(firebase.child("locations"));
    private MagnetApi magnet = MagnetApi.getInstance();
    private User currentUser;
    private AuthData authData;

    private static FirebaseApiClient instance = new FirebaseApiClient();

    private FirebaseApiClient() {
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

    // Retrieve User information from Authdata. Sets up currentUser and Magnet api client.
    private void setAuthData(AuthData authData) {
        this.authData = authData;
        User currentUser = null;
        if(authData != null) {
            final String userId = (String) authData.getProviderData().get("id");
            currentUser = new User(userId);
            currentUser.setName((authData.getProviderData().get("displayName").toString()));
            currentUser.setProfilePicture((String) authData.getProviderData().get("profileImageURL"));
            MagnetApi.getInstance().login(userId, new Callback<Boolean>() {
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
    public void getPotentialMatches(LatLng latLng, final Callback<List<String>> callback) {
        GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 30);
        final List<String> listUserKeys = new ArrayList<String>();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                listUserKeys.add(key.split("_")[0]);
                //System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                callback.onResult(listUserKeys);
                //System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(FirebaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });

    }

    @Override
    public void sendMessage(String recipientId, String message, Callback<Boolean> callback) {
        if(!isLoggedIn()) {
            Log.e(TAG, "No user found, aborting message.");
            callback.onResult(false);
            return;
        }
        magnet.sendMessage(currentUser.getId(), recipientId, message, callback);
        // TODO(tony): Save message to chat here.
    }

    @Override
    public void onReceiveMessage(String senderId, String message, Callback<Boolean> callback) {
        // TODO(tony): Save message to chat here.
        callback.onResult(true);
    }
}
