package huka.com.repli;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.hugo.myapplication.backend.registration.Registration;
import com.example.hugo.myapplication.backend.userInfoApi.UserInfoApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import gcm.GcmRegistrationAsyncTask;

/**
 * Activity for letting the user login or sign up
 * for the application.
 */
public class LoginActivity extends Activity {

    public static final String LOCALHOST_IP = "https://192.168.1.105:8080/_ah/api/";
    public static final String PREF_ACCOUNT_NAME = "accountname";
    private static final int REQUEST_ACCOUNT_PICKER = 2;
    private AutoCompleteTextView usernameTextView;
    private EditText passwordTextView;
    private static final String TAG = "LoginActivity";
    private SharedPreferences settings;
    private GoogleAccountCredential credential;
    private String accountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkCredentials();
        setContentView(R.layout.activity_login);
        usernameTextView = (AutoCompleteTextView) findViewById(R.id.usernameTextView);
        passwordTextView = (EditText) findViewById(R.id.passwordTextView);
    }

    private void checkCredentials() {
        settings = getSharedPreferences("RepliAccount", 0);
        credential = GoogleAccountCredential.usingAudience(this,
                "815657678459-u8cauf778bpal3r0tefu7psrvui9tafl.apps.googleusercontent.com");
            setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        UserInfoApi.Builder builder = new UserInfoApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null).setRootUrl(LOCALHOST_IP);
        UserInfoApi service = builder.build();

        if (credential.getSelectedAccountName() != null) {
            startMain();
        } else {
            chooseAccount();
        }
    }

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.startActivity(intent);
    }

    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    private void setSelectedAccountName(String accountName) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.commit();
        credential.setSelectedAccountName(accountName);
        this.accountName = accountName;
    }

        /**
     * Lets the user log in to the application.
     * A request is sent to the server with the
     * username and password for verification.
     * @param view
     */
    public void loginListener(View view) {
        String username = usernameTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        /* Send REST request to server with username + password.
           Use local test for front end only.
         */
        checkCredentials();
        if(!(username.equals("") && password.equals(""))) {
            displayErrorDialog();
        } else {
            Log.v("as", this.getPackageName());
        }
    }

    private void displayErrorDialog() {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Wrong username or password");
        dlgAlert.setTitle("Could not login");
        dlgAlert.setPositiveButton("Try Again", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    /**
     * Lets the user sign up for the application.
     * A new activity is started so that the user
     * can input credentials.
     * @param view
     */
    public void signUpListener(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (data != null && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        setSelectedAccountName(accountName);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                        new GcmRegistrationAsyncTask(this).execute();
                    }
                }
                break;
        }
    }


}
