package servercalls;

import android.os.AsyncTask;

import com.example.hugo.myapplication.backend.replyInfoApi.ReplyInfoApi;
import com.example.hugo.myapplication.backend.userInfoApi.UserInfoApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import huka.com.repli.LoginActivity;
import huka.com.repli.ReplyInfo;

/**
 * Created by hugo on 3/25/15.
 */
public class RemoveReplyAsyncTask extends AsyncTask<ReplyInfo, Void, Void> {

    private ReplyInfoApi replyService;

    @Override
    protected Void doInBackground(ReplyInfo... params) {
        try {
            ReplyInfo replyInfo = params[0];
            replyService.remove(LoginActivity.accountName, replyInfo.getUsername()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void buildService() {
        ReplyInfoApi.Builder builder2 = new ReplyInfoApi.Builder(AndroidHttp.newCompatibleTransport(),
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
        replyService = builder2.build();
    }
}
