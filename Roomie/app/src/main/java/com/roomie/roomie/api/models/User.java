package com.roomie.roomie.api.models;

import java.util.HashMap;
import java.util.List;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class User {

    public User(String id){
        this.id = id;
    }

    public void retrieve(){

    }

    public void put(){

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        return matches;
    }

    public void addMatch(String match) {
        this.matches.add(match);
    }

    private String id;
    private String profilePicture;
    private String name;
    private String email;

    private List<String> rejectList;
    private List<String> acceptList;
    private List<String> matches;
}
