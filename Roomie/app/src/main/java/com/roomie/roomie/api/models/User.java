package com.roomie.roomie.api.models;

import android.telecom.Call;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.core.GeoHash;
import com.roomie.roomie.ui.Autocomplete;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class User {

    public User(String id){
        this.id = id;
        this.userReference = ref.child("users").child(this.id);
    }

    public User() {
        this("100003674382044");
    }

    public void put() {
        this.userReference.child("name").setValue(this.name);
        this.userReference.child("profilePicture").setValue(this.profilePicture);
        this.userReference.child("rejectList").setValue(this.rejectList);
        this.userReference.child("acceptList").setValue(this.acceptList);
        this.userReference.child("matchesList").setValue(this.matchesList);
    }

    public void retrieve(final Callback callback){
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
        this.put();
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
        this.put();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.put();
    }

    public List<String> getRejectList() {
        return rejectList;
    }

    public void reject(String noList) {
        if (noList != this.id) {
            this.rejectList.add(noList);
            this.put();
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

    public boolean isMatch(String id){
        if (matchesList.contains(id)){
            return true;
        }
        return false;
    }

    public void accept(String id) {
        if (id == this.getId()){
            return;
        }
        User userMatched = new User(id);
        userMatched.retrieve(new Callback() {
            @Override
            public void onResult(User user) {
                if ( user.accepted(User.this.id) ){
                    user.acceptList.remove(User.this.id);
                    user.matchesList.add(User.this.id);
                    User.this.matchesList.add(user.id);
                    user.put();
                    User.this.put();
                }
                else{
                    User.this.acceptList.add(user.id);
                    User.this.put();
                }
            }
        });

    }

    public List<String> getMatches() {
        return matchesList;
    }

    public void addMatch(String match) {
        this.matchesList.add(match);
        this.put();
    }

    private String id;
    private String profilePicture;
    private String name;
    private Firebase userReference;

    private List<String> rejectList = new ArrayList<String>();
    private List<String> acceptList = new ArrayList<String>();
    private List<String> matchesList = new ArrayList<String>();

    Firebase ref = new Firebase("https://theroomieapp.firebaseio.com/");

    public interface Callback {
        void onResult(User user);
    }
}
