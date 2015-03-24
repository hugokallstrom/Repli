package com.example.hugo.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hugo on 3/17/15.
 */
@Entity
public class UserInfo {

    @Id
    Long id;

    @Index
    private String gcmId;

    @Index
    private String accountName;

    @Index
    private String email;

    @Index
    private String profilePictureUrl;

    @Index
    private HashMap<String, String> replyList;

    public UserInfo() {
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public HashMap<String, String> getReplyList() {
        return replyList;
    }

    public void setReplyList(HashMap<String, String> replyList) {
        this.replyList = replyList;
    }

    public String getPicture(String gcmId) {
        return replyList.get(gcmId);
    }

    public void setPicture(String gcmId, String pictureUrl) {
        replyList.put(gcmId, pictureUrl);
    }
}
