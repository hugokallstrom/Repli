package com.example.hugo.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.lang.reflect.Array;
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
        name = "replyInfoApi",
        version = "v1",
        resource = "replyInfo",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.hugo.example.com",
                ownerName = "backend.myapplication.hugo.example.com",
                packagePath = ""
        )
)
public class ReplyInfoEndpoint {

    private static final Logger logger = Logger.getLogger(ReplyInfoEndpoint.class.getName());
    private Objectify objectify;

    private static final int DEFAULT_LIST_LIMIT = 20;

    /**
     * Returns the {@link ReplyInfo} with the corresponding ID.
     *
     * @param myAccountName the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code ReplyInfo} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "replyInfo/{myAccountName}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<ReplyInfo> get(@Named("myAccountName") String myAccountName) throws NotFoundException {
        logger.info("Getting ReplyInfo with ID: " + myAccountName);
        objectify = OfyService.ofy();
        List<ReplyInfo> replyInfo = objectify.load().type(ReplyInfo.class).filter("myAccountName", myAccountName).limit(DEFAULT_LIST_LIMIT).list();
        if (replyInfo == null) {
            throw new NotFoundException("Could not find ReplyInfo with ID: " + myAccountName);
        }
        return replyInfo;
    }

    /**
     * Inserts a new {@code ReplyInfo}.
     */
    @ApiMethod(
            name = "insert",
            path = "replyInfo",
            httpMethod = ApiMethod.HttpMethod.POST)
    public ReplyInfo insert(ReplyInfo replyInfo) {
        objectify = OfyService.ofy();
        objectify.save().entity(replyInfo).now();
        logger.info("Created ReplyInfo.");
        return ofy().load().entity(replyInfo).now();
    }

    @ApiMethod(name = "replied")
    public ReplyInfo update(@Named("accountName") String myAccountName, @Named("receiverAccountName") String receiverAccountName) {
        objectify = OfyService.ofy();
        ReplyInfo replyInfo = objectify.load().type(ReplyInfo.class)
                .filter("myAccountName", myAccountName)
                .filter("accountName", receiverAccountName).first().now();
        replyInfo.setReplied(false);
        objectify.save().entity(replyInfo).now();
        logger.info("set replied false for: " + replyInfo.getAccountName());
        return replyInfo;
    }

    /**
     * Deletes the specified {@code ReplyInfo}.
     *
     * @param myAccountName the ID of the entity to delete
     * @throws com.google.api.server.spi.response.NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code ReplyInfo}
     */
    @ApiMethod(
            name = "remove",
            path = "replyInfo/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") String myAccountName) throws NotFoundException {
        checkExists(myAccountName);
        objectify = OfyService.ofy();
        objectify.delete().type(ReplyInfo.class).id(myAccountName).now();
        logger.info("Deleted ReplyInfo with ID: " + myAccountName);
    }

    private void checkExists(String myAccountName) throws NotFoundException {
        objectify = OfyService.ofy();
        try {
            objectify.load().type(ReplyInfo.class).filter("myAccountName", myAccountName).first().now();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find ReplyInfo with ID: " + myAccountName);
        }
    }
}