package com.roomie.roomie.api.models;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.roomie.roomie.api.Callback;
import com.roomie.roomie.api.FirebaseApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by tonyjhuang on 10/10/15.
 * User tony = new User("tonyid");
 * tony.setName("frank")
 */
public class User {

    public User(String id){
        this.id = id;
        this.userReference = FirebaseApiClient.firebase.child("users").child(this.id);
    }

    public User() {
        this("100003674382044");
    }

    public void retrieve(final Callback<User> callback){
        this.userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User.this.name = snapshot.child("name").getValue().toString();
                User.this.profilePicture = snapshot.child("profilePicture").getValue().toString();
                User.this.matchesList.clear();
                for (DataSnapshot match : snapshot.child("matchesList").getChildren()) {
                    User.this.matchesList.add(match.getValue().toString());
                }
                User.this.acceptList.clear();
                for (DataSnapshot accept : snapshot.child("acceptList").getChildren()) {
                    User.this.acceptList.add(accept.getValue().toString());
                }
                User.this.rejectList.clear();
                for (DataSnapshot reject : snapshot.child("rejectList").getChildren()) {
                    User.this.rejectList.add(reject.getValue().toString());
                }
                callback.onResult(User.this);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.userReference.child("name").setValue(this.name);
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
        this.userReference.child("profilePicture").setValue(this.profilePicture);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.userReference.child("name").setValue(this.name);
    }

    public List<String> getRejectList() {
        return rejectList;
    }

    public void reject(String noList) {
        if (!noList.equals(this.id)) {
            this.rejectList.add(noList);
            this.userReference.child("rejectList").setValue(this.rejectList);
        }
    }

    public List<String> getAcceptList() {
        return acceptList;
    }

    public boolean accepted(String id){
        if (acceptList.contains(id)){
            return true;
        }
        return false;
    }

    public boolean rejected(String id){
        if (rejectList.contains(id)){
            return true;
        }
        return false;
    }

    public boolean isMatch(String id){
        if (matchesList.contains(id)){
            return true;
        }
        return false;
    }

    public void addMatch(String id){
        if (matchesList.contains(id) || id.equals(this.getId())){
            return;
        }
        matchesList.add(id);
        this.userReference.child("matchesList").setValue(this.matchesList);
    }

    public void accept(String id, final Callback<User> callback) {
        if (id.equals(this.getId())){
            return;
        }
        User userMatched = new User(id);
        userMatched.retrieve(new Callback<User>() {
            @Override
            public void onResult(User user) {
                if (user.accepted(User.this.id)) {
                    user.acceptList.remove(User.this.id);
                    user.userReference.child("acceptList").setValue(user.acceptList);
                    user.addMatch(User.this.id);
                    User.this.addMatch(user.getId());
                    callback.onResult(User.this);
                } else {
                    User.this.acceptList.add(user.id);
                    User.this.userReference.child("acceptList").setValue(User.this.acceptList);
                    callback.onResult(User.this);
                }
            }
        });

    }

    public List<String> getMatches() {
        return matchesList;
    }

    public int getAge() {
        return new Random().nextInt(20) + 18;
    }

    private String id;
    private String profilePicture;
    private String name;
    private Firebase userReference;

    private List<String> rejectList = new ArrayList<String>();
    private List<String> acceptList = new ArrayList<String>();
    private List<String> matchesList = new ArrayList<String>();
}
