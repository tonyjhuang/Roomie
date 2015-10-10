package com.roomie.roomie.api.models;

import java.util.HashMap;
import java.util.List;

/**
 * Created by tonyjhuang on 10/10/15.
 */
public class User {
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

    public List<String> getNoList() {
        return noList;
    }

    public void setNoList(List<String> noList) {
        this.noList = noList;
    }

    public List<String> getYesList() {
        return yesList;
    }

    public void setYesList(List<String> yesList) {
        this.yesList = yesList;
    }

    public List<String> getMatches() {
        return matches;
    }

    public void setMatches(List<String> matches) {
        this.matches = matches;
    }

    private String id;
    private String profilePicture;
    private String name;
    private String email;

    private List<String> noList;
    private List<String> yesList;
    private List<String> matches;
}
