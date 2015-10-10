package com.roomie.roomie.api.models;

import android.telecom.Call;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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

    public void put(){
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
                User.this.name = snapshot.child("name").toString();
                User.this.profilePicture = snapshot.child("profilePicture").toString();
                User.this.matchesList.clear();
                for (DataSnapshot match: snapshot.child("matchesList").getChildren()){
                    User.this.matchesList.add(match.toString());
                }
                User.this.acceptList.clear();
                for (DataSnapshot accept: snapshot.child("acceptList").getChildren()){
                    User.this.acceptList.add(accept.toString());
                }
                User.this.rejectList.clear();
                for (DataSnapshot reject: snapshot.child("rejectList").getChildren()){
                    User.this.rejectList.add(reject.toString());
                }
                callback.onResult(User.this);

//                System.out.println(snapshot.child("profilePicture"));
////                System.out.println("There are " + snapshot.getChildrenCount() + " blog posts");
////                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
////                    BlogPost post = postSnapshot.getValue(BlogPost.class);
////                    System.out.println(post.getAuthor() + " - " + post.getTitle());
////                }
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
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRejectList() {
        return rejectList;
    }

    public void reject(String noList) {
        this.rejectList.add(noList);
    }

    public List<String> getAcceptList() {
        return acceptList;
    }

    public void accept(String yesList) {
        this.acceptList.add(yesList);
    }

    public List<String> getMatches() {
        return matchesList;
    }

    public void addMatch(String match) {
        this.matchesList.add(match);
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
