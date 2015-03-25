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
    private Integer[] fullImages =  new Integer[] {R.drawable.test_image5, R.drawable.test_image3, R.drawable.test_image1};;

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
            /* send to server in order to retrieve images
               from other users and display in this fragment.
               For now, display a place holder progressbar
               and load three images from memory.
      */
//            System.out.println(data.getData());
            UploadPicToRandomAsyncTask asyncTask = new UploadPicToRandomAsyncTask(this.getActivity());
          // WeakReference<MyAsyncTask> asyncTaskWeakRef = new WeakReference<>(asyncTask);
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
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mDataset.get(position).getImage().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();

                Intent intent = new Intent(getActivity(), ViewReplyActivity.class);
                intent.putExtra("picture", b);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public class MyAsyncTask extends AsyncTask<String, Void, Void> {

      //  private WeakReference<CameraFragment> fragmentWeakRef;

        private MyAsyncTask () {
           // this.fragmentWeakRef = new WeakReference<>(fragment);
        }

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
      //      progressDialog = ProgressDialog.show(getActivity(), "Wait", "Finding images around the world...");
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
        @Override
        protected Void doInBackground(String... params) {
            System.out.println("myAsyncTask!");
            String url = params[0];
//            String url = "http://"+LoginActivity.LOCALHOST_IP2+":8080/_ah/img/"+blobKey;
            Bitmap picture = getBitmapFromURL(url);
            mDataset.clear();
                // Load and scale images
                BitmapDecoder bitmapDecoder = new BitmapDecoder(getActivity());

                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(picture, bitmapDecoder.getScreenWidth(), 600);
                ReplyInfo replyInfo = new ReplyInfo("tester");
                replyInfo.setImage(picture);
                replyInfo.setThumbnail(thumbImage);
                mDataset.add(replyInfo);

            return null;
        }

        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
//            progressDialog.dismiss();
        //    if (this.fragmentWeakRef.get() != null) {
                mAdapter.notifyDataSetChanged();
        //    }
        }
    }


    //register your activity onResume()
    @Override
    public void onResume() {
        super.onResume();
        this.getActivity().registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
    }

    //Must unregister onPause()
    @Override
    public void onPause() {
        super.onPause();
        this.getActivity().unregisterReceiver(mMessageReceiver);
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            System.out.println("cameraFrag " + message);
            String url = "http://"+LoginActivity.LOCALHOST_IP2+":8080/_ah/img/"+message;
            System.out.println(url);
            new MyAsyncTask().execute(url);
        }
    };
}
