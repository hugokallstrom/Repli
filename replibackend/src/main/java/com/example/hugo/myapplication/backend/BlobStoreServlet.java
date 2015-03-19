package com.example.hugo.myapplication.backend;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hugo on 3/19/15.
 */
public class BlobStoreServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(UserInfoEndpoint.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        logger.info("called blob servlet!!! ");
        List<BlobKey> blobs = BSService.blobStore().getUploads(req).get("file");
        BlobKey blobKey = blobs.get(0);

        ImagesService imagesService = ImagesServiceFactory.getImagesService();
        ServingUrlOptions servingOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);

        String servingUrl = imagesService.getServingUrl(servingOptions);

        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType("application/json");

        JSONObject json = new JSONObject();
        json.put("servingUrl", servingUrl);
        json.put("blobKey", blobKey.getKeyString());

        PrintWriter out = res.getWriter();
        out.print(json.toString());
        out.flush();
        out.close();
    }
}
