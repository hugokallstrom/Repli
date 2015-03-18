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
     //   scopes = {AuthorizationConstants.EMAIL_SCOPE},
     //   clientIds = {AuthorizationConstants.ANDROID_CLIENT_ID},
     //   audiences = {AuthorizationConstants.ANDROID_AUDIENCE},
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
      // ObjectifyService.register(UserInfo.class);
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
    @ApiMethod(name = "register")
    public UserInfo registerUser(UserInfo userInfo) {
        logger.info("Call to register");
      //  try {
           // checkUserParameters(userInfo);
            Objectify objectify = OfyService.ofy();
            objectify.save().entity(userInfo).now();
            logger.info("Created UserInfo.");
            return objectify.load().entity(userInfo).now();
        /*} catch (InvalidPropertiesFormatException e) {
            logger.info(e.getMessage());
            return null;
        }*/
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
     */
    @ApiMethod(name = "unregister")
    public void unregister(@Named("accountName") String accountName) throws NotFoundException {
        UserInfo userinfo = ofy().load().type(UserInfo.class).filter("accountName", accountName).first().now();
        if(userinfo.id != null) {
            ofy().delete().type(UserInfo.class).id(userinfo.id).now();
            logger.info("Deleted UserInfo with ID: " + userinfo.id);
        }
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

    private void checkUserParameters(UserInfo userInfo) throws InvalidPropertiesFormatException {
        if(ofy().load().type(UserInfo.class).filter("accountName", userInfo.getAccountName()).first().now() != null) {
            throw new InvalidPropertiesFormatException("accnount name already exists");
        } else if(ofy().load().type(UserInfo.class).filter("email", userInfo.getEmail()).first().now() != null) {
            throw new InvalidPropertiesFormatException("email already exists");
        } else if(ofy().load().type(UserInfo.class).filter("gcmId", userInfo.getGcmId()).first().now() != null) {
            throw new InvalidPropertiesFormatException("gcm id already exists");
        }
    }
}