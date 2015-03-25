package servercalls;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;

/**
 * Created by hugo on 3/25/15.
 */
public class UploadImage {

    public static HttpResponse uploadImage(String profilePictureUrl, File imageFile) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        System.out.printf("url ::: " + profilePictureUrl);
        HttpPost httppost = new HttpPost(profilePictureUrl);
        FileBody fileBody  = new FileBody(imageFile.getAbsoluteFile());
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        reqEntity.addPart("file", fileBody);
        httppost.setEntity(reqEntity);

        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = httpclient.execute(httppost);
        httpclient.getConnectionManager().shutdown();
        return response;
    }
}
