package com.roomie.roomie.api;

import android.util.Log;

import com.magnet.mmx.client.api.MMX;
import com.magnet.mmx.client.api.MMXMessage;
import com.magnet.mmx.client.api.MMXUser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class MagnetAPI {
    private static final String TAG = "Magnet";
    private static final byte[] PASSWORD = "password".getBytes();
    private static boolean loggedIn = false;

    private static MagnetAPI instance;

    private MagnetAPI() {
    }

    public static MagnetAPI getInstance() {
        if (instance == null) {
            instance = new MagnetAPI();
        }
        return instance;
    }

    public void login(final String username, final Callback<Boolean> callback) {
        if (loggedIn) {
            if (callback != null) {
                callback.onResult(true);
            }
            return;
        }

        MMXUser user = new MMXUser.Builder().username(username).build();
        user.register(PASSWORD, new MMXUser.OnFinishedListener<Void>() {
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "registered");
                MMX.login(username, PASSWORD, new MMX.OnFinishedListener<Void>() {
                    public void onSuccess(Void aVoid) {
                        //success!
                        //if an EventListener has already been registered, start the MMX messaging service
                        MMX.start();
                        Log.d(TAG, "logged in as " + username);
                        loggedIn = true;
                        if (callback != null) {
                            callback.onResult(true);
                        }
                    }

                    public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                        if (MMX.FailureCode.SERVER_AUTH_FAILED.equals(failureCode)) {
                            Log.e(TAG, "Failed to log in.");
                            if (callback != null) {
                                callback.onResult(false);
                            }
                        }
                    }
                });
            }

            public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {

                if (MMXUser.FailureCode.REGISTRATION_INVALID_USERNAME.equals(failureCode)) {
                    //handle registration failure
                    Log.e(TAG, "Failed to register, invalid username");
                    if (callback != null) {
                        callback.onResult(false);
                    }

                } else if (MMXUser.FailureCode.REGISTRATION_USER_ALREADY_EXISTS.equals(failureCode)) {
                    // User already exists. just login.
                    MMX.login(username, PASSWORD, new MMX.OnFinishedListener<Void>() {
                        public void onSuccess(Void aVoid) {
                            //success!
                            //if an EventListener has already been registered, start the MMX messaging service
                            MMX.start();
                            Log.d(TAG, "logged in as " + username);
                            loggedIn = true;
                            if (callback != null) {
                                callback.onResult(true);
                            }
                        }

                        public void onFailure(MMX.FailureCode failureCode, Throwable throwable) {
                            if (MMX.FailureCode.SERVER_AUTH_FAILED.equals(failureCode)) {
                                Log.e(TAG, "Failed to log in.");
                                if (callback != null) {
                                    callback.onResult(false);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public void sendMessage(
            final String senderUserName,
            final String recipientUserName,
            final String messageText,
            final Callback<Boolean> callback) {
        login(senderUserName, new Callback<Boolean>() {
            @Override
            public void onResult(Boolean loggedIn) {
                if (!loggedIn) {
                    Log.e(TAG, "Couldn't login as " + senderUserName + ". Message not sent.");
                    if (callback != null) {
                        callback.onResult(false);
                    }
                    return;
                }

                MMXUser.getUser(recipientUserName, new MMXUser.OnFinishedListener<MMXUser>() {
                    @Override
                    public void onSuccess(MMXUser mmxUser) {
                        Log.d(TAG, "Got user: " + recipientUserName);
                        // Set up message params.
                        HashSet<MMXUser> recipients = new HashSet<MMXUser>();
                        recipients.add(mmxUser);
                        HashMap<String, String> content = new HashMap<String, String>();
                        content.put("key", messageText);

                        MMXMessage message = new MMXMessage.Builder()
                                .recipients(recipients)
                                .content(content)
                                .build();

                        message.send(new MMXMessage.OnFinishedListener<String>() {
                            public void onSuccess(String s) {
                                Log.d(TAG, "Sent message!");
                                if (callback != null) {
                                    callback.onResult(true);
                                }
                            }

                            public void onFailure(MMXMessage.FailureCode failureCode, Throwable e) {
                                Log.e(TAG, "Failed to send message... " + e.getMessage());
                                if (callback != null) {
                                    callback.onResult(false);
                                }
                            }

                        });
                    }

                    @Override
                    public void onFailure(MMXUser.FailureCode failureCode, Throwable throwable) {
                        Log.e(TAG, "Failed to get user " + recipientUserName);
                        if (callback != null) {
                            callback.onResult(false);
                        }
                    }
                });

            }
        });
    }

    public MMX.EventListener getEventListener(final Callback<String> callback) {
        return new MMX.EventListener() {
            public boolean onMessageReceived(MMXMessage message) {
                for (Map.Entry<String, String> entry : message.getContent().entrySet()) {
                    Log.d(TAG, "message received: " + entry.getValue());
                    callback.onResult(entry.getValue());
                }
                return false;
            }
        };
    }
}
