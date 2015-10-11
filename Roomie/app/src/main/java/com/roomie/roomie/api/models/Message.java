package com.roomie.roomie.api.models;

import com.firebase.client.Firebase;
import com.roomie.roomie.api.FirebaseApi;
import com.roomie.roomie.api.FirebaseApiClient;

import java.util.Date;

/**
 * Created by Rina on 10/10/15.
 */
public class Message {
    private boolean sentByAuthor;
    private String text;



    public Message(){
    }

    public void sendMessage(String currentUserId, String recipient, String text){
        Firebase users_dir = FirebaseApiClient.firebase.child("users");
        Firebase message_dir = users_dir.child(currentUserId).child("messages");
        this.sentByAuthor = true;
        this.text = text;
        message_dir.child(recipient).push().setValue(this);
    }

    public void receiveMessage(String currentUserId, String sender, String text){
        Firebase users_dir = FirebaseApiClient.firebase.child("users");
        Firebase message_dir = users_dir.child(currentUserId).child("messages");
        this.sentByAuthor = false;
        this.text = text;
        message_dir.child(sender).push().setValue(this);
    }
}
