package com.example.javaapp;

import android.net.Uri;

public class Users {
    String profilePic, email, username, userId, status, password, lastMessage;

    public Users() {}

    public Users(String profilePic, String email, String username, String userId, String status, String password) {
        this.profilePic = profilePic;
        this.email = email;
        this.username = username;
        this.userId = userId;
        this.status = status;
        this.password = password;
    }

//    public Users() {}

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
