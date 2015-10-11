package com.roomie.roomie.api.models;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;
import com.roomie.roomie.api.Callback;
import com.roomie.roomie.api.FirebaseApi;
import com.roomie.roomie.api.FirebaseApiClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rina on 10/10/15.
 */
public class Message {

    private String text;
    private boolean sentByAuthor;

    public Message(){
    }

    public void sendMessage(String currentUserId, String recipient, String text){
        Firebase users_dir = FirebaseApiClient.firebase.child("users");
        Firebase message_dir = users_dir.child(currentUserId).child("messages");
        Map<String, String> mess = new HashMap<String, String>();
        mess.put("message", text);
        mess.put("sentByAuthor", "true");
        message_dir.child(recipient).push().setValue(mess);
    }

    public void receiveMessage(String currentUserId, String sender, String text){
        Firebase users_dir = FirebaseApiClient.firebase.child("users");
        Firebase message_dir = users_dir.child(currentUserId).child("messages");
        Map<String, String> mess = new HashMap<String, String>();
        mess.put("message", text);
        mess.put("sentByAuthor", "false");
        message_dir.child(sender).push().setValue(mess);
    }

    public void getMessageHistory(String currentUserId, String recipient, final Callback<List<HashMap<String,String>>> callback){
        Firebase users_dir = FirebaseApiClient.firebase.child("users");
        Firebase message_dir = users_dir.child(currentUserId).child("messages").child(recipient);
        final List<HashMap<String, String>> ret = new ArrayList<>();

        message_dir.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot id : snapshot.getChildren()) {
                    ret.add((HashMap<String, String>) id.getValue());
                }
                callback.onResult(ret);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void getLastMessage(String currentUserId, String recipient, final Callback<String> callback){
        Firebase users_dir = FirebaseApiClient.firebase.child("users");
        Firebase message_dir = users_dir.child(currentUserId).child("messages").child(recipient);
        message_dir.addListenerForSingleValueEvent(new ValueEventListener() {
            String ret;
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot id : snapshot.getChildren()) {
                    ret = id.child("message").getValue().toString();
                    //ret.add((HashMap<String, String>) id.getValue());
                }
                callback.onResult(ret);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}
