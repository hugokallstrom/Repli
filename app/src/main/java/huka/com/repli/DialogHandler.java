package huka.com.repli;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.plus.Plus;

/**
 * Displays dialogs.
 */
public class DialogHandler {
    public static void logoutDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Exit")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.app_name), 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(LoginActivity.ACCOUNT_NAME, "");
                        editor.commit();
                        if (LoginActivity.mGoogleApiClient.isConnected()) {
                            Plus.AccountApi.clearDefaultAccount(LoginActivity.mGoogleApiClient);
                            LoginActivity.mGoogleApiClient.disconnect();
                            LoginActivity.mGoogleApiClient.connect();
                        }
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}
