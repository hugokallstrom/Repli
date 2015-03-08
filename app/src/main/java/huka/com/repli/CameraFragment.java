package huka.com.repli;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import adapters.MyRecyclerCameraAdapter;
import floatingactionbuttonbasic.FloatingActionButton;
import views.RoundedImageView;


public class CameraFragment extends android.support.v4.app.Fragment {

    protected RecyclerView mRecyclerView;
    protected MyRecyclerCameraAdapter mAdapter;
    protected ArrayList<Bitmap> mDataset = new ArrayList<>();
    FragmentActivity mActivity;
    private WeakReference<MyAsyncTask> asyncTaskWeakRef;

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
            /* send to server in order to retrieve images
               from other users and display in this fragment.
               For now, display a place holder progressbar
               and load three images from memory.
             */
            MyAsyncTask asyncTask = new MyAsyncTask(this);
            this.asyncTaskWeakRef = new WeakReference<>(asyncTask);
            asyncTask.execute();
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
                mDataset.get(position).compress(Bitmap.CompressFormat.JPEG, 30, baos);
                byte[] b = baos.toByteArray();

                Intent intent = new Intent(getActivity(), ViewReplyActivity.class);
                intent.putExtra("picture", b);
               // startActivity(intent);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<CameraFragment> fragmentWeakRef;

        private MyAsyncTask (CameraFragment fragment) {
            this.fragmentWeakRef = new WeakReference<>(fragment);
        }

        private ProgressDialog progressDialog;
        private Bitmap image1;
        private Bitmap image2;
        private Bitmap image3;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "Wait", "Finding images around the world...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            // These images should come from a server
            image1 = BitmapFactory.decodeResource(getResources(),
                    R.drawable.image1);
            image2 = BitmapFactory.decodeResource(getResources(),
                    R.drawable.image2);
            image3 = BitmapFactory.decodeResource(getResources(),
                    R.drawable.image3);
            mDataset.clear();
            mDataset.add(image1);
            mDataset.add(image2);
            mDataset.add(image3);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            progressDialog.dismiss();
            if (this.fragmentWeakRef.get() != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

}
