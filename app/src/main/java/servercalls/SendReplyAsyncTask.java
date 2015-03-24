package servercalls;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by hugo on 3/24/15.
 */
public class SendReplyAsyncTask extends AsyncTask<String, Void, String>  {


    private final Context context;

    public SendReplyAsyncTask(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(String... params) {
        String gcmId = params[0];
        String pictureUrl = params[0];
        return null;
    }

    @Override
    protected void onPostExecute(String url) {
        if(!url.equals("")) {
            Toast.makeText(context, "Profile picture changed", Toast.LENGTH_LONG).show();
        }
    }

}
