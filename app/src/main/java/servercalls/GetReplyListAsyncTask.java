package servercalls;

import android.os.AsyncTask;

import com.example.hugo.myapplication.backend.replyInfoApi.ReplyInfoApi;
import com.example.hugo.myapplication.backend.replyInfoApi.model.ReplyInfo;
import com.example.hugo.myapplication.backend.replyInfoApi.model.ReplyInfoCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import huka.com.repli.LoginActivity;

/**
 * Created by hugo on 3/24/15.
 */
public class GetReplyListAsyncTask extends AsyncTask<String, Void, List<ReplyInfo>> {

    private ReplyInfoApi replyService;

    @Override
    protected List<ReplyInfo> doInBackground(String... params) {
        String accountName = params[0];
        if (replyService == null) {
            buildService();
            try {
                buildTestReplyInfo(accountName);
                ReplyInfoCollection replyInfoCollection = replyService.get(accountName).execute();
                List<ReplyInfo> replyInfoList = replyInfoCollection.getItems();
                System.out.println("Replylist: " + replyInfoList.toString());
                return replyInfoCollection.getItems();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void buildTestReplyInfo(String accountName) {
        try {
            ReplyInfo replyInfo = new ReplyInfo();
            replyInfo.setMyAccountName(accountName);
            replyInfo.setAccountName("linus@gmail.com");
            replyInfo.setGcmId("1231531123");
            replyInfo.setTimeStamp("2015-03-21 12:23");
            replyInfo.setReplied(false);
            replyService.insert(replyInfo).execute();

            ReplyInfo replyInfo2 = new ReplyInfo();
            replyInfo2.setMyAccountName(accountName);
            replyInfo2.setAccountName("apan@gmail.com");
            replyInfo2.setGcmId("1209023");
            replyInfo2.setReplied(true);
            replyInfo2.setTimeStamp("2015-02-12 11:53");
            replyService.insert(replyInfo2).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildService() {
        ReplyInfoApi.Builder builder = new ReplyInfoApi.Builder(AndroidHttp.newCompatibleTransport(),
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
        replyService = builder.build();
    }
}
