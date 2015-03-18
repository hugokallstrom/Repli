package gcm;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.hugo.myapplication.backend.registration.Registration;
import com.example.hugo.myapplication.backend.registration.model.RegistrationRecord;
import com.example.hugo.myapplication.backend.userInfoApi.UserInfoApi;
import com.example.hugo.myapplication.backend.userInfoApi.model.UserInfo;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import huka.com.repli.LoginActivity;
import huka.com.repli.MainActivity;

public class GcmRegistrationAsyncTask extends AsyncTask<String, Void, String> {
    private static UserInfoApi regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;
    private static final String SENDER_ID = "815657678459";

    public GcmRegistrationAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
       if (regService == null) {
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
        registerToService(params);
        return registerToGcm();
    }

    private void registerToService(String... params) {
        UserInfo userInfo = new UserInfo();
        userInfo.setGcmId(registerToGcm());
        userInfo.setAccountName(params[0]);
        userInfo.setEmail(params[0]);
        userInfo.setProfilePictureUrl(null);
        try {
            UserInfo userInfoResp = regService.register(userInfo).setPrettyPrint(true).execute();
            Log.v("register", "registered with id: " + userInfoResp.getEmail());
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return regId;
    }

    @Override
    protected void onPostExecute(String msg) {
       // Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

        /*Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);*/
    }
}