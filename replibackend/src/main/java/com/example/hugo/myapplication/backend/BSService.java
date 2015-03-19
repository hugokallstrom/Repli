package com.example.hugo.myapplication.backend;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * Created by hugo on 3/19/15.
 */
public class BSService {

    static {
        BlobstoreServiceFactory.getBlobstoreService();
    }

    public static BlobstoreService blobStore() {
        return BlobstoreServiceFactory.getBlobstoreService();
    }
}
