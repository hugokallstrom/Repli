package servercalls;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.hugo.myapplication.backend.randomListApi.RandomListApi;
import com.example.hugo.myapplication.backend.randomListApi.model.RandomList;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import huka.com.repli.LoginActivity;
import huka.com.repli.R;

/**
 * Created by Linus on 2015-03-24.
 */

public class UploadPicToRandomAsyncTask extends AsyncTask<File, Void, String>  {

    private static RandomListApi regService = null;
    private Context context;

    public UploadPicToRandomAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(File... params) {
        File imageFile = params[0];
        String url = "";
        System.out.println("do back");
        if (regService == null) {

            buildService();
            try {
                System.out.println("regserv");
                RandomList replys = regService.getUploadUrl(getAccountName()).execute();
                HttpResponse response = uploadImage((String) replys.getPictures().get(getAccountName()), imageFile);
                url = saveToDB(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    private HttpResponse uploadImage(String profilePictureUrl, File imageFile) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        System.out.printf("url ::: " + profilePictureUrl);
        HttpPost httppost = new HttpPost(profilePictureUrl);
        FileBody fileBody = new FileBody(imageFile.getAbsoluteFile());
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        reqEntity.addPart("file", fileBody);
        httppost.setEntity(reqEntity);

        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = httpclient.execute(httppost);
        httpclient.getConnectionManager().shutdown();
        return response;
    }

    private String saveToDB(HttpResponse response) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        String profilePictureUrl = jsonObject.getString("servingUrl");
        String accountName = getAccountName();

        String[] parts = profilePictureUrl.split("/_ah/img/");
      //  String part1 = parts[0];
        String blobKey = parts[1];
        System.out.println("Adding picture! " + profilePictureUrl + "BLOBKEY " + blobKey        );
        RandomList randomList = regService.addPicture(blobKey, accountName).execute();
        RandomList randomList2 = regService.addPicture("kalle", "linyx5@gmail.com").execute();
        RandomList randomList3 = regService.addPicture("kalle", "linyx6@gmail.com").execute();
        RandomList randomList4 = regService.addPicture("kalle", "linyx7@gmail.com").execute();
        RandomList randomList5 = regService.addPicture("kalle", "linyx8@gmail.com").execute();
        System.out.println("profpic: " + profilePictureUrl + " accName;: " + accountName);
        String url = (String) randomList.getPictures().get(getAccountName());
        System.out.println("URL: " + url);
        return "";
    }

    private String getAccountName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "none");
        return username;
    }

    private void buildService() {
        RandomListApi.Builder builder = new RandomListApi.Builder(AndroidHttp.newCompatibleTransport(),
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
    protected void onPostExecute(String url) {
        if (!url.equals("")) {
            Toast.makeText(context, "Profile picture changed", Toast.LENGTH_LONG).show();
        }
    }
}
