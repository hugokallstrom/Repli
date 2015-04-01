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

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            userService = ServiceBuilder.buildUserInfoService();
            replyService = ServiceBuilder.buildReplyInfoService();
            try {
                String pictureUrl = getPictureUrl();
                UserInfo userInfoSender = userService.getUser(LoginActivity.accountName).execute();
                UserInfo userInfoReceiver = userService.getUser(receiverAccountName).execute();

                // set gcmId to receivers gcmId
                userInfoSender.setGcmId(userInfoReceiver.getGcmId());
                ReplyInfo replyInfo = buildReplyInfo(userInfoSender, pictureUrl);
                replyService.insert(replyInfo).execute();
                replyService.replied(LoginActivity.accountName, receiverAccountName).execute();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getPictureUrl() throws IOException, JSONException {
        UserInfo userInfoUrl = userService.getUploadUrl().execute();
        HttpResponse response = UploadImage.uploadImage(userInfoUrl.getProfilePictureUrl(), new File(imagePath));
        JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
        return jsonObject.getString("servingUrl");
    }

    private ReplyInfo buildReplyInfo(UserInfo userInfo, String pictureUrl) {
        ReplyInfo replyInfo = new ReplyInfo();
        replyInfo.setMyAccountName(receiverAccountName);
        replyInfo.setAccountName(userInfo.getAccountName());
        replyInfo.setGcmId(userInfo.getGcmId());
        replyInfo.setProfilePictureUrl(userInfo.getProfilePictureUrl());
        replyInfo.setPictureUrl(pictureUrl);
        replyInfo.setReplied(true);
        replyInfo.setTimeStamp(getDate());
        return replyInfo;
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        return dateFormat.format(date);
    }

    @Override
    protected void onPostExecute(String url) {

    }

}
