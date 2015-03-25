package com.example.hugo.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Objectify;
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
        name = "randomListApi",
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
public class RandomListEndpoint {

    private static final Logger logger = Logger.getLogger(RandomListEndpoint.class.getName());
    private Objectify objectify;
    private static final int DEFAULT_LIST_LIMIT = 20;

    // TODO Change the returned url when deploying
    @ApiMethod(name = "getUploadUrl")
    public RandomList getUploadUrl(@Named("accName") String accName) {
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        String blobUploadUrl = blobstoreService.createUploadUrl("/blob/upload");
        System.out.println(blobUploadUrl);
        blobUploadUrl = blobUploadUrl.replace("debian", AuthorizationConstants.LOCAL_IP);
        System.out.println(blobUploadUrl);
        logger.info("bloburl: " + blobUploadUrl);
        RandomList reply = new RandomList();
        reply.setProfilePictureUrl(accName, blobUploadUrl);
        return reply;
    }

    @ApiMethod(name = "addPicture")
    public RandomList addPicture(@Named("pictureUrl") String pictureUrl, @Named("accountName") String accountName) {
        System.out.println("--------- in add picture!");
        CollectionResponse<RandomList> replys = list(null,100);
        System.out.println(replys.getItems().size());
        RandomList rep = new RandomList();
        if(replys.getItems().size() > 0 ){
            System.out.println("size of replylist > 0");
            for (RandomList randomList : replys.getItems()) {
                if(randomList.getPictures().size() < 4){
                    System.out.println("Set rep = reply");
                    rep = randomList;
                    break;
                }
            }
        }

        rep.setPictures(accountName, pictureUrl);
        objectify = OfyService.ofy();
        objectify.save().entity(rep).now();
        logger.info("saved profile picture for " + accountName + " with url" +  rep.getPictures().get(accountName));
        return rep;
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
            path = "replyList",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<RandomList> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<RandomList> query = ofy().load().type(RandomList.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<RandomList> queryIterator = query.iterator();
        List<RandomList> randomListList = new ArrayList<RandomList>(limit);
        while (queryIterator.hasNext()) {
            randomListList.add(queryIterator.next());
        }
        return CollectionResponse.<RandomList>builder().setItems(randomListList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(RandomList.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find ReplyList with ID: " + id);
        }
    }
}