package servercalls;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.hugo.myapplication.backend.randomListApi.RandomListApi;
import com.example.hugo.myapplication.backend.randomListApi.model.RandomList;

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

    private static RandomListApi randomService = null;
    private String email;
    private Context context;

    public UploadPicToRandomAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(File... params) {
        File imageFile = params[0];
        String url = "";
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        email = sharedPreferences.getString(LoginActivity.EMAIL, "none");
        System.out.println("ACCOUNTNAME: " + email);

        if (randomService == null) {
            System.out.println("reg serv == null");
            randomService = ServiceBuilder.buildRandomListService();
        } else {
            System.out.println("reg serv != null");
        }
            try {
                System.out.println("regserv");
                RandomList replys = randomService.getUploadUrl(email).execute();
                HttpResponse response = uploadImage((String) replys.getPictures().get(email), imageFile);
                url = saveToDB(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        return url;
    }

    private HttpResponse uploadImage(String profilePictureUrl, File imageFile) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        System.out.printf("url ->: " + profilePictureUrl);
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

        String[] parts = profilePictureUrl.split(".com/");
      //  String part1 = parts[0];
        String blobKey = parts[1];
        System.out.println("Adding picture! " + profilePictureUrl + "BLOBKEY " + blobKey        );
        RandomList randomList = randomService.addPicture(blobKey, email).execute();
        System.out.println("profpic: " + profilePictureUrl + " email: " + email);
        String url = (String) randomList.getPictures().get(email);
        System.out.println("URL: " + url);
        return "";
    }

}
