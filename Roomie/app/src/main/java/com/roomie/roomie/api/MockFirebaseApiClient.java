package com.roomie.roomie.api;

import com.roomie.roomie.api.models.User;

import java.util.ArrayList;
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

    public static List<User> mockUsers = new ArrayList<>();
    static {
        User tony = new User("733994556733017");
        tony.setProfilePicture("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xpa1/v/t1.0-1/11295684_672150022917471_7841526459019478803_n.jpg?oh=b065e1f61f62bc08b5229f51e05590ac&oe=56990AD8&__gda__=1452967836_4ec18ad34ed59ff0fb518b66638ef6e2");
        tony.setName("Tony");
        mockUsers.add(tony);
        User rina = new User("1271901889502313");
        rina.setProfilePicture("https://scontent.xx.fbcdn.net/hprofile-xft1/v/t1.0-1/11665373_1204540846238418_8187500592839999169_n.jpg?oh=975407b7ad0cb867a77bbe2517e0dadc&oe=56C8CA12");
        rina.setName("Marina");
        mockUsers.add(rina);
        User ola = new User("100000612016996");
        ola.setProfilePicture("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xfa1/t31.0-1/c362.106.1324.1324/s720x720/272382_238910722805993_263604_o.jpg");
        ola.setName("Ola");
        mockUsers.add(ola);
        User christian = new User("100000755704753");
        christian.setProfilePicture("https://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash2/v/t1.0-1/1240137_559835827384936_789079275_n.jpg?oh=e5ff5f47ee085d352a1fef4765b1fd9c&oe=568945C0&__gda__=1452379486_69b01253338cff11bf8f219069a1d0b6");
        christian.setName("Christian");
        mockUsers.add(christian);
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

    }

    @Override
    public void getPotentialMatches(Callback<List<User>> callback) {
        callback.onResult(mockUsers);
    }

    @Override
    public void sendMessage(String recipientId, String message, Callback<Boolean> callback) {

    }

    @Override
    public void onReceiveMessage(String senderId, String message, Callback<Boolean> callback) {

    }
}
