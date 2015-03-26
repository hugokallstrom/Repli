package huka.com.repli;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import adapters.MyRecyclerCameraAdapter;
import floatingactionbuttonbasic.FloatingActionButton;
import servercalls.UploadPicToRandomAsyncTask;

/**
 * Handles the "Camera" tab which lets the user
 * take a photo and view the photos after sending
 * a picture.
 */
public class CameraFragment extends android.support.v4.app.Fragment {

    protected RecyclerView mRecyclerView;
    protected MyRecyclerCameraAdapter mAdapter;
    protected ArrayList<ReplyInfo> mDataset = new ArrayList<>();
    FragmentActivity mActivity;

    protected static final int CAPTURE_IMAGE_REQUEST_CODE = 1;
    private File file;

    public static CameraFragment newInstance() {
        return new CameraFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_camera_frag, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerCameraView);

        File dir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(dir+"/"+"reply.jpg");
        FloatingActionButton takePhotoButton = (FloatingActionButton) rootView.findViewById(R.id.takePhotoButton);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(imageIntent, CAPTURE_IMAGE_REQUEST_CODE);
            }
        });

        mAdapter = new MyRecyclerCameraAdapter(mDataset);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode != 0) {
            UploadPicToRandomAsyncTask asyncTask = new UploadPicToRandomAsyncTask(this.getActivity());
            asyncTask.execute(file);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewCreated(View view , Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.SetOnItemClickListener(new MyRecyclerCameraAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), ViewReplyActivity.class);
                intent.putExtra("picture", mDataset.get(position).getImage());
                intent.putExtra("accountName", mDataset.get(position).getUsername());
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public class MyAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            System.out.println("myAsyncTask!");
            String url = params[0];
            String account = params[1];
//            String url = "http://"+LoginActivity.LOCALHOST_IP2+":8080/_ah/img/"+blobKey;
            mDataset.clear();
            ReplyInfo replyInfo = new ReplyInfo(account);
            replyInfo.setImage(url);
            replyInfo.setThumbnail(url);

            mDataset.add(replyInfo);

            return null;
        }

        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getActivity().registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.getActivity().unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String account = intent.getStringExtra("account");
            System.out.println("cameraFrag " + message);
            String url = "http://"+LoginActivity.LOCALHOST_IP2+":8080/_ah/img/"+message;
            System.out.println(url);
            new MyAsyncTask().execute(url, account);
        }
    };
}
