package com.example.hugo.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;

/**
 * Created by hugo on 3/18/15.
 */
@Entity
public class ReplyList {

    @Id
    Long id;

    @Index
    private String accountName;

    @Index
    private String gcmId;

    @Index
    private String profilePictureUrl;

    @Index
    private String timeStamp;

    @Index
    private Boolean replied;



}
