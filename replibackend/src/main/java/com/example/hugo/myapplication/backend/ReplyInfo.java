package com.example.hugo.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;

/**
 * Created by hugo on 3/18/15.
 */
@Entity
public class ReplyInfo {

    @Id
    Long id;

    @Index
    private String myAccountName;

    @Index
    private String accountName;

    @Index
    private String gcmId;

    @Index
    private String profilePictureUrl;

    @Index
    private String timeStamp;

    @Index
    private String pictureUrl;

    @Index
    private Boolean replied;


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

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Boolean getReplied() {
        return replied;
    }

    public void setReplied(Boolean replied) {
        this.replied = replied;
    }

    public String getMyAccountName() {
        return myAccountName;
    }

    public void setMyAccountName(String myAccountName) {
        this.myAccountName = myAccountName;
    }
}
