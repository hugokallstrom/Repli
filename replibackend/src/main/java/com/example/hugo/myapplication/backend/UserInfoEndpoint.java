package com.example.hugo.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;


@Api(
        name = "userInfoApi",
        version = "v1",
        scopes = {AuthorizationConstants.EMAIL_SCOPE},
        clientIds = {AuthorizationConstants.ANDROID_CLIENT_ID},
        audiences = {AuthorizationConstants.ANDROID_AUDIENCE},
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.hugo.example.com",
                ownerName = "backend.myapplication.hugo.example.com",
                packagePath = ""
        )
)
public class UserInfoEndpoint {

    private static final Logger logger = Logger.getLogger(UserInfoEndpoint.class.getName());
    private Objectify objectify;

    @ApiMethod(name = "isRegistered")
    public UserInfo isRegistered(@Named("accountName") String accountName) {
        try {
            checkExists(accountName);
            UserInfo userInfo = new UserInfo();
            userInfo.setAccountName(accountName);
            return userInfo;
        } catch (NotFoundException e) {
            logger.info(e.getMessage());
            return null;
        }
    }
    /**
     * Inserts a new {@code UserInfo}.
     */
    @ApiMethod(name = "register")
    public UserInfo registerUser(UserInfo userInfo) {
        logger.info("Call to register");
        try {
            checkUserParameters(userInfo);
            objectify = OfyService.ofy();
            objectify.save().entity(userInfo).now();
            logger.info("Created UserInfo.");
            return objectify.load().entity(userInfo).now();
        } catch (InvalidPropertiesFormatException e) {
            logger.info(e.getMessage());
            return null;
        }
    }

    /**
     * Deletes the specified {@code UserInfo}.
     */
    @ApiMethod(name = "unregister")
    public void unregister(@Named("accountName") String accountName) throws NotFoundException {
        UserInfo userinfo = ofy().load().type(UserInfo.class).filter("accountName", accountName).first().now();
        if(userinfo.id != null) {
            ofy().delete().type(UserInfo.class).id(userinfo.id).now();
            logger.info("Deleted UserInfo with ID: " + userinfo.id);
        }
    }

    // TODO Change the returned url when deploying
    @ApiMethod(name = "getUploadUrl")
    public UserInfo getUploadUrl() {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        String blobUploadUrl = blobstoreService.createUploadUrl("/blob/upload");
        blobUploadUrl = blobUploadUrl.replace("Maria-Dator", AuthorizationConstants.LOCAL_IP);
        logger.info("bloburl: " + blobUploadUrl);
        UserInfo userInfo = new UserInfo();
        userInfo.setProfilePictureUrl(blobUploadUrl);
        return userInfo;
    }

    @ApiMethod(name = "getUser")
    public UserInfo getUser(@Named("accountName") String accountName) {
        UserInfo userInfo = null;
        try {
            userInfo = checkExists(accountName);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    @ApiMethod(name = "addProfilePicture")
    public UserInfo addProiflePicture(UserInfo userInfo) {
        String accountName = userInfo.getAccountName();
        String profilePictureUrl = userInfo.getProfilePictureUrl();
        objectify = OfyService.ofy();
        UserInfo loadedUserInfo = objectify.load().type(UserInfo.class).filter("accountName", accountName).first().now();
        loadedUserInfo.setProfilePictureUrl(profilePictureUrl);
        objectify.save().entity(loadedUserInfo).now();
        logger.info("saved profile picture for " + accountName + " with url" + profilePictureUrl);
        return loadedUserInfo;
    }

    private UserInfo checkExists(String accountName) throws NotFoundException {
        objectify = OfyService.ofy();
        UserInfo userInfo = objectify.load().type(UserInfo.class).filter("accountName", accountName).first().now();
        if(userInfo == null) {
            logger.info("user " + accountName + " not found");
            throw new NotFoundException("account not found");
        }
        logger.info("user " + accountName + " found");
        return userInfo;
    }

    private void checkUserParameters(UserInfo userInfo) throws InvalidPropertiesFormatException {
        objectify = OfyService.ofy();
        if(objectify.load().type(UserInfo.class).filter("accountName", userInfo.getAccountName()).first().now() != null) {
            throw new InvalidPropertiesFormatException("accnount name already exists");
        } else if(objectify.load().type(UserInfo.class).filter("email", userInfo.getEmail()).first().now() != null) {
            throw new InvalidPropertiesFormatException("email already exists");
        } else if(objectify.load().type(UserInfo.class).filter("gcmId", userInfo.getGcmId()).first().now() != null) {
            throw new InvalidPropertiesFormatException("gcm id already exists");
        }
    }
}