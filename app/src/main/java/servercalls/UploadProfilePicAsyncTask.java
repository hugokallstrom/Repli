package servercalls;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.hugo.myapplication.backend.userInfoApi.UserInfoApi;
import com.example.hugo.myapplication.backend.userInfoApi.model.UserInfo;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import huka.com.repli.LoginActivity;

/**
 * Created by hugo on 3/19/15.
 */
public class UploadProfilePicAsyncTask extends  AsyncTask<File, Void, Boolean> {
    private static UserInfoApi regService = null;
    private Context context;

    public UploadProfilePicAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(File... params) {
        File imageFile = params[0];
        if (regService == null) {
            buildService();
            try {
                UserInfo userInfo = regService.getUploadUrl().execute();
                String jsonString = uploadImage(userInfo.getProfilePictureUrl(), imageFile);
                saveResponse(jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String uploadImage(String profilePictureUrl, File imageFile) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(profilePictureUrl);
        FileBody fileBody  = new FileBody(imageFile.getAbsoluteFile());
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        reqEntity.addPart("file", fileBody);

        httppost.setEntity(reqEntity);

        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity resEntity = response.getEntity();
        httpclient.getConnectionManager().shutdown();
        return EntityUtils.toString(response.getEntity());
    }

    private void saveResponse(String jsonString) throws JSONException {
        JSONObject jsonObj = new JSONObject(jsonString);
        String servingUrl = jsonObj.getString("servingUrl");
        String blobKey = jsonObj.getString("blobKey");
//        regService.setProfilePicture(servingUrl, blobKey);
    }

    private void buildService() {
        UserInfoApi.Builder builder = new UserInfoApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null) //.setRootUrl("https://repliapp.appspot.com/_ah/api/");
                // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                // otherwise they can be skipped
                .setRootUrl(LoginActivity.LOCALHOST_IP)
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                            throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        regService = builder.build();
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        if(bool) {
            Toast.makeText(context, "Profile picture changed", Toast.LENGTH_LONG).show();
        }
    }
}
