package huka.com.repli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity for letting the user login or sign up
 * for the application.
 */
public class LoginActivity extends Activity {

    private Button loginButton;
    private Button signUpButton;
    private AutoCompleteTextView usernameTextView;
    private EditText passwordTextView;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signupButton);
        usernameTextView = (AutoCompleteTextView) findViewById(R.id.usernameTextView);
        passwordTextView = (EditText) findViewById(R.id.passwordTextView);
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
        Log.v(TAG, "username: " + username);
        Log.v(TAG, "password: " + password);
        // Send REST to server with username + password
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Lets the user sign up for the application.
     * A new activity is started so that the user
     * can input credentials.
     * @param view
     */
    public void signUpListener(View view) {

    }


}
