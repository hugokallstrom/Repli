package com.example.hugo.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "userInfoApi",
        version = "v1",
        resource = "userInfo",
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

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(UserInfo.class);
    }

    /**
     * Returns the {@link UserInfo} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code UserInfo} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "userInfo/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public UserInfo get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting UserInfo with ID: " + id);
        UserInfo userInfo = ofy().load().type(UserInfo.class).id(id).now();
        if (userInfo == null) {
            throw new NotFoundException("Could not find UserInfo with ID: " + id);
        }
        return userInfo;
    }

    /**
     * Inserts a new {@code UserInfo}.
     */
    @ApiMethod(
            name = "register",
            path = "userInfo",
            httpMethod = ApiMethod.HttpMethod.POST)
    public UserInfo register(UserInfo userInfo) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that userInfo.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        if(checkUserParameters(userInfo)) {
            ofy().save().entity(userInfo).now();
            logger.info("Created UserInfo.");
            return ofy().load().entity(userInfo).now();
        } else {
            logger.info("User exists");
            return null;
        }
    }

    /**
     * Updates an existing {@code UserInfo}.
     *
     * @param id       the ID of the entity to be updated
     * @param userInfo the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code UserInfo}
     */
    @ApiMethod(
            name = "update",
            path = "userInfo/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public UserInfo update(@Named("id") Long id, UserInfo userInfo, User user) throws NotFoundException, OAuthRequestException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        if(user != null) {
            checkExists(id);
            ofy().save().entity(userInfo).now();
            logger.info("Updated UserInfo: " + userInfo);
            return ofy().load().entity(userInfo).now();
        } else {
            throw new OAuthRequestException("Could not validate request");
        }
    }

    /**
     * Deletes the specified {@code UserInfo}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code UserInfo}
     */
    @ApiMethod(
            name = "remove",
            path = "userInfo/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(UserInfo.class).id(id).now();
        logger.info("Deleted UserInfo with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "userInfo",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<UserInfo> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<UserInfo> query = ofy().load().type(UserInfo.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<UserInfo> queryIterator = query.iterator();
        List<UserInfo> userInfoList = new ArrayList<UserInfo>(limit);
        while (queryIterator.hasNext()) {
            userInfoList.add(queryIterator.next());
        }
        return CollectionResponse.<UserInfo>builder().setItems(userInfoList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(UserInfo.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find UserInfo with ID: " + id);
        }
    }

    private Boolean checkUserParameters(UserInfo userInfo) {
        Boolean valid = true;
        if(OfyService.ofy().load().type(RegistrationRecord.class).filter("accountName", userInfo.getAccountName()).first().now() != null) {
            valid = false;
        } else if(OfyService.ofy().load().type(RegistrationRecord.class).filter("email", userInfo.getEmail()).first().now() != null) {
            valid = false;
        } else if(OfyService.ofy().load().type(RegistrationRecord.class).filter("gcmId", userInfo.getGcmId()).first().now() != null) {
            valid = false;
        }
        return valid;
    }
}