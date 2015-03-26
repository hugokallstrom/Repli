package huka.com.repli;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.hugo.myapplication.backend.userInfoApi.UserInfoApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import servercalls.UserRegistrationAsyncTask;

/**
 * Activity for letting the user login or sign up
 * for the application.
 */
public class LoginActivity extends Activity {

    public static final String LOCALHOST_IP = "http://192.168.1.71:8080/_ah/api/";
    public static final String LOCALHOST_IP2 = "192.168.1.71";
    public static final String PREF_ACCOUNT_NAME = "accountName";
    private static final int REQUEST_ACCOUNT_PICKER = 2;
    private static final String TAG = "LoginActivity";
    private SharedPreferences settings;
    private GoogleAccountCredential credential;
    public static String accountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkCredentials();
        setContentView(R.layout.activity_login);
    }

    private void checkCredentials() {
        settings = getSharedPreferences(getString(R.string.app_name), 0);
        credential = GoogleAccountCredential.usingAudience(this,
                "815657678459-u8cauf778bpal3r0tefu7psrvui9tafl.apps.googleusercontent.com");
        setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        String accountName = settings.getString(PREF_ACCOUNT_NAME, null);
        System.out.println("accname: " + accountName);
        if (accountName != null && !accountName.equals("")) {
            setSelectedAccountName(accountName);
            LoginActivity.accountName = accountName;
            startMain();
            Log.v(TAG, "found credential, starting main");
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

    private void setSelectedAccountName(String credAccountName) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, credAccountName);
        editor.apply();
        credential.setSelectedAccountName(credAccountName);
        accountName = credAccountName;
    }

    public void loginListener(View view) {
        chooseAccount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (data != null && data.getExtras() != null) {
                    accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        setSelectedAccountName(accountName);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        new UserRegistrationAsyncTask(this).execute(accountName);
                    }
                }
                break;
        }
    }


}
