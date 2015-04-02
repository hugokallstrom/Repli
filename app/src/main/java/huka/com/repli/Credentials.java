package huka.com.repli;

import android.content.Context;

/**
 * Created by hugo on 4/2/15.
 */
public class Credentials {

    public static String getEmail(Context context) {
        return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).getString(LoginActivity.EMAIL, "none");
    }
}
