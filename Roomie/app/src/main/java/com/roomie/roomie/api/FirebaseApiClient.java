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
import com.roomie.roomie.api.models.Location;
import com.roomie.roomie.api.models.Message;
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
    private Message messageModel = new Message();

    private static FirebaseApiClient instance = new FirebaseApiClient();

    public void resetData() {
        User ola = new User("100000612016996");
        ola.setProfilePicture("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xfa1/t31.0-1/c362.106.1324.1324/s720x720/272382_238910722805993_263604_o.jpg");
        ola.setName("Ola Spyra");
        ola.setBio("Hi! I'm from Poland and I love watermelons :D PS: Im never home.");
        new Location(ola.getId(), new LatLng(40.712784, -74.005941), "NewYork");
        new Location(ola.getId(), new LatLng(27.664827, -81.515754), "Florida");
        ola.clearArrays();
        User christian = new User("100000755704753");
        christian.setProfilePicture("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/v/t1.0-1/1240137_559835827384936_789079275_n.jpg?oh=e5ff5f47ee085d352a1fef4765b1fd9c&oe=568945C0&__gda__=1452379486_69b01253338cff11bf8f219069a1d0b6");
        christian.setName("Christian Dullweber");
        christian.setBio("Moving to California for an internship. Looking for quiet and clean roommate.");
        new Location(christian.getId(), new LatLng(36.054445, -112.140111), "grandCanyon");
        new Location(christian.getId(), new LatLng(36.169941, -115.139830), "Las Vegas");
        christian.clearArrays();
        User david = new User("1439577444");
        david.setProfilePicture("https://scontent-sjc2-1.xx.fbcdn.net/hphotos-xap1/t31.0-8/1507440_10201113373303348_1245057313_o.jpg");
        david.setName("David Brown");
        david.setBio("I prefer living by myself but you are welcome to crash in my place if you have to.");
        david.clearArrays();
        david.accept("1271901889502313", new Callback<User>() {
            @Override
            public void onResult(User result) {
            }
        });
        new Location(david.getId(), new LatLng(33.683947, -117.794694), "Irvine");
        new Location(david.getId(), new LatLng(37.368830, -122.036350), "Sunnyvale");
        User ally = new User("1292436436");
        ally.setProfilePicture("https://scontent-sjc2-1.xx.fbcdn.net/hphotos-xta1/v/t1.0-9/11118506_10204327981901650_6048266467256362903_n.jpg?oh=e9c5a9d0a511cacc04d1d637574f21eb&oe=568520F5");
        ally.setName("Allison Kim");
        new Location(ally.getId(), new LatLng(42.360082, -71.058880), "Boston");
        new Location(ally.getId(), new LatLng(40.058324, -74.405661), "Jersey");
        ally.setBio("WHAT ARE THOOOOOOOOOOOOOOOOOOSE?");
        ally.clearArrays();
        ally.accept("733994556733017", new Callback<User>() {
            @Override
            public void onResult(User result) {
            }
        });
        User jacob = new User("735156277");
        jacob.setProfilePicture("https://scontent-sjc2-1.xx.fbcdn.net/hphotos-xlp1/t31.0-8/11224840_10153430472741278_289750567507577625_o.jpg");
        jacob.setName("Jacob Sharf");
        new Location(jacob.getId(), new LatLng(42.360082, -71.058880), "Mountain View");
        new Location(jacob.getId(), new LatLng(40.058324, -74.405661), "San Francisco");
        jacob.clearArrays();
        jacob.setBio("Looking for a a place up to $700/month.");
        User jonathan = new User("735156277");
        jonathan.setProfilePicture("https://scontent-sjc2-1.xx.fbcdn.net/hphotos-ash2/t31.0-8/1911938_10153468476624057_2634192120274430307_o.jpg");
        jonathan.setName("Jonathan Wu");
        jonathan.setBio("I like rock climbing and diving. :D No pets.");
        new Location(jonathan.getId(), new LatLng(43.464258, -80.520410), "Waterloo,ON");
        new Location(jonathan.getId(), new LatLng(37.338208, -121.886329), "San Jose");
        jonathan.clearArrays();
        jonathan.accept("1271901889502313", new Callback<User>() {
            @Override
            public void onResult(User result) {
            }
        });
        User franck = new User("100000184352342");
        franck.setBio("");
        franck.setProfilePicture("https://scontent-sjc2-1.xx.fbcdn.net/hphotos-xap1/v/t1.0-9/11006417_1107984442551024_3173246781727446957_n.jpg?oh=0e43c8f5f8155f5daeb5463251594715&oe=569E3E22");
        franck.setName("Gilles Franck");
        new Location(franck.getId(), new LatLng(42.886447, -78.878369), "Buffalo");
        new Location(franck.getId(), new LatLng(37.441883, -122.143019), "Palo Alto");
        franck.clearArrays();
        franck.setBio("I want to make new friends :)");
        User tony = new User("733994556733017");
        tony.clearArrays();
        tony.clearMessages();
        tony.setBio("Hi I'm the most friendly roommate ever :D");
        User rina = new User("1271901889502313");
        rina.setProfilePicture("https://scontent.xx.fbcdn.net/hprofile-xft1/v/t1.0-1/11665373_1204540846238418_8187500592839999169_n.jpg?oh=975407b7ad0cb867a77bbe2517e0dadc&oe=56C8CA12");
        rina.setName("Marina Coimbra");
        rina.setBio("Crazy cat lady from Brazil. Looking for a place for me and my two baby cats <3.");
        rina.clearArrays();
        rina.clearMessages();

    }

    private FirebaseApiClient() {
        // Check if we have a cached auth.
        this.resetData();
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
    public String getCurrentUserId(){
        return currentUser.getId();
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
        currentUser.retrieve(callback);
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
        messageModel.sendMessage(currentUser.getId(), recipientId, message);
        messageModel.receiveMessage(recipientId, currentUser.getId(), message);
    }

    @Override
    public void onReceiveMessage(String senderId, String message, Callback<Boolean> callback) {
        Log.d(TAG, "RECEIVING MESSAGE!!!!");
        //messageModel.receiveMessage(currentUser.getId(), senderId, message);
        callback.onResult(true);
    }
}
