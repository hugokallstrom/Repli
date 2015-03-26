package servercalls;

import com.example.hugo.myapplication.backend.randomListApi.RandomListApi;
import com.example.hugo.myapplication.backend.replyInfoApi.ReplyInfoApi;
import com.example.hugo.myapplication.backend.userInfoApi.UserInfoApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import huka.com.repli.LoginActivity;

/**
 * Created by hugo on 3/26/15.
 */
public class ServiceBuilder {

    public static ReplyInfoApi buildReplyInfoService() {
        ReplyInfoApi.Builder builder = new ReplyInfoApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null).setRootUrl("https://repliapp.appspot.com/_ah/api/");
                   /* .setRootUrl(LoginActivity.LOCALHOST_IP)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });*/
        return builder.build();
    }

    public static UserInfoApi buildUserInfoService() {
        UserInfoApi.Builder builder = new UserInfoApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null).setRootUrl("https://repliapp.appspot.com/_ah/api/");
               /* .setRootUrl(LoginActivity.LOCALHOST_IP)
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                            throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });*/
        return builder.build();
    }

    public static RandomListApi buildRandomListService() {
        RandomListApi.Builder builder = new RandomListApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null).setRootUrl("https://repliapp.appspot.com/_ah/api/");
               /* .setRootUrl(LoginActivity.LOCALHOST_IP)
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                            throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });*/
        return builder.build();
    }
}
