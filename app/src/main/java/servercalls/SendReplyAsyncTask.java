package servercalls;

import android.content.Context;
import android.os.AsyncTask;

import com.example.hugo.myapplication.backend.replyInfoApi.ReplyInfoApi;
import com.example.hugo.myapplication.backend.replyInfoApi.model.ReplyInfo;
import com.example.hugo.myapplication.backend.userInfoApi.UserInfoApi;
import com.example.hugo.myapplication.backend.userInfoApi.model.UserInfo;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import huka.com.repli.LoginActivity;

/**
 * Created by hugo on 3/24/15.
 */
public class SendReplyAsyncTask extends AsyncTask<String, Void, String>  {

    private UserInfoApi userService;
    private ReplyInfoApi replyService;
    private final Context context;
    private String imagePath;
    private String receiverAccountName;

    public SendReplyAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        imagePath = params[0];
        receiverAccountName = params[1];
        if (userService == null) {
            buildService();
            try {
                UserInfo userInfoUrl = userService.getUploadUrl().execute();
                //UploadImage.uploadImage(userInfoUrl.getProfilePictureUrl(), imagePath);
                UserInfo userInfo = userService.getUser(LoginActivity.accountName).execute();
                buildReplyInfo(userInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private ReplyInfo buildReplyInfo(UserInfo userInfo) {
        ReplyInfo replyInfo = new ReplyInfo();
        replyInfo.setMyAccountName(receiverAccountName);
        replyInfo.setAccountName(userInfo.getAccountName());
        replyInfo.setGcmId(userInfo.getGcmId());
        replyInfo.setProfilePictureUrl(userInfo.getProfilePictureUrl());
        return replyInfo;
    }

    private void buildService() {
        UserInfoApi.Builder builder = new UserInfoApi.Builder(AndroidHttp.newCompatibleTransport(),
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
        userService = builder.build();

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

    @Override
    protected void onPostExecute(String url) {

    }

}
