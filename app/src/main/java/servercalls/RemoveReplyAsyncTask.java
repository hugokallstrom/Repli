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
        if (replyService == null) {
            replyService = ServiceBuilder.buildReplyInfoService();
            try {
                ReplyInfo replyInfo = params[0];
                replyService.remove(LoginActivity.accountName, replyInfo.getUsername()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
