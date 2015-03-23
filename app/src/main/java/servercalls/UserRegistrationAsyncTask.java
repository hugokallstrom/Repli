package servercalls;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.hugo.myapplication.backend.userInfoApi.UserInfoApi;
import com.example.hugo.myapplication.backend.userInfoApi.model.UserInfo;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import huka.com.repli.LoginActivity;
import huka.com.repli.MainActivity;
import huka.com.repli.R;
import huka.com.repli.UserInfoActivity;

public class UserRegistrationAsyncTask extends AsyncTask<String, Void, Boolean> {
    private static UserInfoApi regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;
    private static final String SENDER_ID = "815657678459";

    public UserRegistrationAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        buildService();
        if (!isRegistered(params[0])) {
            String gcmId = registerToGcm();
            UserInfo userInfo = buildUserInfo(gcmId, params);
            registerToService(userInfo);
            saveUsername(params);
            return true;
        }
        Log.v("register", "already registered");
        return false;
    }

    private Boolean isRegistered(String accountName) {
        try {
            UserInfo userInfo = regService.isRegistered(accountName).execute();
            if(userInfo == null) {
                Log.v("register", "not found");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("register", "found");
        return true;
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

    private UserInfo buildUserInfo(String gcmId, String... params) {
        UserInfo userInfo = new UserInfo();
        userInfo.setGcmId(gcmId);
        userInfo.setAccountName(params[0]);
        userInfo.setEmail(params[0]);
        userInfo.setProfilePictureUrl(null);
        return userInfo;
    }

    private void registerToService(UserInfo userInfo) {
        try {
            UserInfo userInfoResp = regService.register(userInfo).execute();
            if(userInfoResp != null) {
                Log.v("register", "registered with id: " + userInfoResp.getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String registerToGcm() {
        String regId = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            regId = gcm.register(SENDER_ID);
            String msg = "Device registered, registration ID=" + regId;
            Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("GCM_ID", regId).apply();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return regId;
    }

    private void saveUsername(String[] params) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("USERNAME", params[0]).apply();
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        if(bool) {
            Toast.makeText(context, "Successfully registered", Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }
}