package gcm;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.hugo.repli.backend.registration.Registration;
import com.example.hugo.repli.backend.registration.model.CollectionResponseRegistrationRecord;
import com.example.hugo.repli.backend.registration.model.RegistrationRecord;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import huka.com.repli.MainActivity;

public class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
    private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;

    private static final String SENDER_ID = "815657678459";

    public GcmRegistrationAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (regService == null) {
            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null).setRootUrl("https://repliapp.appspot.com/_ah/api/");
                // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                // otherwise they can be skipped
             /*   .setRootUrl("http://192.168.1.105:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                            throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });*/
                // end of optional local run code

            regService = builder.build();

        }

        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            String regId = gcm.register(SENDER_ID);
            msg = "Device registered, registration ID=" + regId;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            regService.register(regId).execute();
            CollectionResponseRegistrationRecord listDevices = regService.listDevices(10).execute();
            for (RegistrationRecord registrationRecord : listDevices.getItems()) {
                Log.v("IDS: ", registrationRecord.getRegId());
            }


        } catch (IOException ex) {
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }
}