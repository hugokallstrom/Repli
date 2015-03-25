package huka.com.repli;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hugo.myapplication.backend.replyInfoApi.ReplyInfoApi;
import com.example.hugo.myapplication.backend.replyInfoApi.model.ReplyInfoCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import adapters.MyRecyclerReplyAdapter;

public class RepliesFragment extends android.support.v4.app.Fragment {

    private static final int DATASET_COUNT = 9;

    protected RecyclerView mRecyclerView;
    protected MyRecyclerReplyAdapter mAdapter;
    protected ArrayList<ReplyInfo> mDataset = new ArrayList<>();
    FragmentActivity mActivity;
    private int[] profilePictures;
    private Integer[] fullImages;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static RepliesFragment newInstance() {
        return new RepliesFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (FragmentActivity) activity;
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profilePictures = new int[] {R.drawable.profile_picture1, R.drawable.profile_picture2, R.drawable.profile_picture3,
                R.drawable.profile_picture4, R.drawable.profile_picture5, R.drawable.profile_picture6,
                R.drawable.profile_picture7, R.drawable.profile_picture8, R.drawable.profile_picture9};
        fullImages = new Integer[] {R.drawable.test_image1, R.drawable.test_image2, R.drawable.test_image3,
                R.drawable.test_image4, R.drawable.test_image5, R.drawable.test_image6, R.drawable.test_image7, R.drawable.test_image8,
                R.drawable.test_image9};

        initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_reply_frag, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerReplyView);
        mAdapter = new MyRecyclerReplyAdapter(mDataset);
        return rootView;
    }

    @Override
    public void onViewCreated(View view , Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.SetOnItemClickListener(new MyRecyclerReplyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v , int position) {

                if(!mDataset.get(position).isReplied()) {
                    return;
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mDataset.get(position).getImage().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();

                Intent intent = new Intent(getActivity(), ViewReplyActivity.class);
                intent.putExtra("picture", b);
                intent.putExtra("accountName", mDataset.get(position).getUsername());
                startActivityForResult(intent, 0);
            }

            @Override
            public boolean onItemLongClicked(int position) {
                final int itemPosition = position;
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getActivity());
                dlgAlert.setTitle("Remove Conversation");
                dlgAlert.setMessage("Do you want to remove this Conversation? (Cannot be undone)");
                dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.removeItem(itemPosition);
                    }
                });
                dlgAlert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dlgAlert.create().show();
                return true;
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary_dark));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        initDataset();
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Generates Data for RecyclerView's adapter. This data would otherwise
     * come from a server.
     */
    private void initDataset() {
        new GetReplyListAsyncTask().execute(LoginActivity.accountName);
        /*LoadImagesTask asyncTask = new LoadImagesTask();
        this.asyncTaskWeakRef = new WeakReference<>(asyncTask);
        asyncTask.execute(DATASET_COUNT);*/
    }

    private class GetReplyListAsyncTask extends AsyncTask<String, Void, Void> {

        private ReplyInfoApi replyService;
        private ProgressDialog progressDialog;

        private GetReplyListAsyncTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "Wait", "Retrieving conversations...");
        }

        @Override
        protected Void doInBackground(String... params) {
            String accountName = params[0];
            mDataset = new ArrayList<>();
            if (replyService == null) {
                buildService();
                try {
                    buildTestReplyInfo(accountName);
                    ReplyInfoCollection replyInfoCollection = replyService.get(accountName).execute();
                    List<com.example.hugo.myapplication.backend.replyInfoApi.model.ReplyInfo> replyInfoList = replyInfoCollection.getItems();
                    System.out.println("Replylist: " + replyInfoList.toString());

                    for (com.example.hugo.myapplication.backend.replyInfoApi.model.ReplyInfo replyInfo : replyInfoList) {
                        BitmapDecoder bitmapDecoder = new BitmapDecoder(getActivity());
                        ReplyInfo replyInfoDataSet = new ReplyInfo(replyInfo.getAccountName());
                        replyInfoDataSet.setDate(replyInfo.getTimeStamp());
                        replyInfoDataSet.setReplied(replyInfo.getReplied());

                        String url = replyInfo.getPictureUrl();
                        String profileUrl = replyInfo.getProfilePictureUrl();
                        if(url.contains("0.0.0.0") || profileUrl.contains("0.0.0.0")) {
                            url = url.replace("0.0.0.0", "130.239.220.166");
                            profileUrl = profileUrl.replace("0.0.0.0", "130.239.220.166");
                        }
                        System.out.println(url);
                        Bitmap picture = getBitmapFromURL(url);
                        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(picture, bitmapDecoder.getScreenWidth(), 200);
                        Bitmap blurredThumbImage = BitmapDecoder.blurBitmap(thumbImage, getActivity());

                        if(!replyInfo.getReplied()) {
                            replyInfoDataSet.setThumbnail(BitmapDecoder.makeBlackAndWhite(blurredThumbImage));
                        } else {
                            replyInfoDataSet.setThumbnail(blurredThumbImage);
                        }

                        replyInfoDataSet.setImage(picture);
                        System.out.println(replyInfo.getProfilePictureUrl());
                        Bitmap profilePicture = getBitmapFromURL(profileUrl);
                        replyInfoDataSet.setProfilePicture(profilePicture);
                        mDataset.add(replyInfoDataSet);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private void buildTestReplyInfo(String accountName) {
            try {
                com.example.hugo.myapplication.backend.replyInfoApi.model.ReplyInfo replyInfo2 = new com.example.hugo.myapplication.backend.replyInfoApi.model.ReplyInfo();
                replyInfo2.setMyAccountName(accountName);
                replyInfo2.setAccountName("apan@gmail.com");
                replyInfo2.setGcmId("1209023");
                replyInfo2.setProfilePictureUrl("http://www.american.edu/uploads/profiles/large/chris_palmer_profile_11.jpg");
                replyInfo2.setPictureUrl("http://www.nature.org/cs/groups/webcontent/@web/@texas/documents/media/prd_011155.jpg");
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

        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            mAdapter.setDataSet(mDataset);
            mAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }

        public Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                myBitmap.getHeight();
                return myBitmap;
            } catch (IOException e) {
                // Log exception
                return null;
            }
        }
    }

}
