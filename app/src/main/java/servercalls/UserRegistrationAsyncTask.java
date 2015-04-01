package servercalls;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.hugo.myapplication.backend.userInfoApi.UserInfoApi;
import com.example.hugo.myapplication.backend.userInfoApi.model.UserInfo;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import huka.com.repli.LoginActivity;
import huka.com.repli.R;

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
        regService = ServiceBuilder.buildUserInfoService();
        if (!isRegistered(params[0])) {
            String gcmId = registerToGcm();
            UserInfo userInfo = buildUserInfo(gcmId, params);
            registerToService(userInfo);
            saveCredentials(params);
            return true;
        }
        Log.v("register", "already registered");
        return false;
    }

    private Boolean isRegistered(String email) {
        Log.v("register", "EMAIl:" + email);
        try {
            UserInfo userInfo = regService.isRegistered(email).execute();
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

    private UserInfo buildUserInfo(String gcmId, String... params) {
        UserInfo userInfo = new UserInfo();
        userInfo.setGcmId(gcmId);
        userInfo.setEmail(params[0]);
        userInfo.setAccountName(params[1]);
        userInfo.setProfilePictureUrl(params[2]);
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

    private void saveCredentials(String[] params) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(LoginActivity.EMAIL, params[0]).apply();
        sharedPreferences.edit().putString(LoginActivity.ACCOUNT_NAME, params[1]).apply();
        sharedPreferences.edit().putString(LoginActivity.PROF_PIC, params[2]).apply();
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        if(bool) {
            Toast.makeText(context, "Successfully registered", Toast.LENGTH_LONG).show();
        }
    }
}