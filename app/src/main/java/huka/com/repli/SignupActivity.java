package huka.com.repli;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;


public class SignupActivity extends Activity {

    private AutoCompleteTextView usernameTextView;
    private AutoCompleteTextView emailTextView;
    private EditText passwordTextView;
    private EditText cpasswordTextView;
    private boolean valid = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        usernameTextView = (AutoCompleteTextView) findViewById(R.id.signup_usernameTextView);
        emailTextView = (AutoCompleteTextView) findViewById(R.id.signup_emailTextView);
        passwordTextView = (EditText) findViewById(R.id.signup_passwordTextView);
        cpasswordTextView = (EditText) findViewById(R.id.signup_cpasswordTextView);
    }

    public void signUpListener(View view) {
        String username = usernameTextView.getText().toString();
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        String confirmedPassword = cpasswordTextView.getText().toString();

        if(checkPassword(password, confirmedPassword) && checkUsername(username) && checkEmail(email)) {
            displaySuccessDialog();
        }
    }

    private boolean checkPassword(String password, String confirmedPassword) {
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmedPassword)) {
            displayErrorDialog("Please enter a password");
            valid = false;
        } else if (!password.equals(confirmedPassword)) {
            displayErrorDialog("Passwords does not match");
            valid = false;
        } else if(password.length() < 5) {
            displayErrorDialog("Password needs to be at least 5 characters long");
            valid = false;
        } else {
            valid = true;
        }

        if(!valid) {
            passwordTextView.setText("");
            cpasswordTextView.setText("");
        }
        return valid;
    }

    private boolean checkUsername(String username) {
        // Call server to check if username exists
        if(TextUtils.isEmpty(username)) {
            valid = false;
            displayErrorDialog("Please enter a username");
        } else if(username.length() > 2) {
            displayErrorDialog("Username must be at least 2 characters long");
        } else {
            valid = true;
        }
        return valid;
    }

    private boolean checkEmail(String email) {
        // Call server to see if email is already registered
        if(TextUtils.isEmpty(email)) {
            valid = false;
            displayErrorDialog("Please enter an email address");
        } else if(!(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            valid = false;
            displayErrorDialog("Invalid email address");
            emailTextView.setText("");
        } else {
            valid = true;
        }
        return valid;
    }

    private void displayErrorDialog(String errorText) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(errorText);
        dlgAlert.setTitle("Could not sign up");
        dlgAlert.setPositiveButton("Try Again", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void displaySuccessDialog() {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setTitle("User created");
        dlgAlert.setMessage("You can now login with your username and password");
        dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        dlgAlert.create().show();
    }
}
