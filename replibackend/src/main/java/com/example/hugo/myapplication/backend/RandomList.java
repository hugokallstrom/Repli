package com.example.hugo.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hugo on 3/18/15.
 */
@Entity
public class RandomList {

    @Id
    Long id;

    @Index
    private Map<String, String> pictures = new HashMap<>();

    public Map<String, String> getPictures() {
        return pictures;
    }

    public void setPictures(String accName, String picture) {
       pictures.put(accName, picture);
    }

    public String getPictureUrl(String accName) {
        return pictures.get(accName);
    }

    public void setProfilePictureUrl(String accName, String profilePictureUrl) {
       pictures.put(accName, profilePictureUrl);
    }


}
