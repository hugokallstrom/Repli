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

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import adapters.MyRecyclerReplyAdapter;
import views.RoundedImageView;


public class RepliesFragment extends android.support.v4.app.Fragment {

    private static final int DATASET_COUNT = 9;

    protected RecyclerView mRecyclerView;
    protected MyRecyclerReplyAdapter mAdapter;
    protected ArrayList<ReplyInfo> mDataset;
    FragmentActivity mActivity;
    private int[] profilePictures;
    private Integer[] fullImages;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private WeakReference<LoadImagesTask> asyncTaskWeakRef;

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
                //startActivity(intent);
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
                        Log.v("Repl", "mDataset length: " + mDataset.size());
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
                        mAdapter.setDataSet(mDataset);
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
        String[] usernames = { "Danne", "Schött", "Jojje", "Limpa", "Hinners",
                               "Hiltan", "Macke", "Suther", "Bophin"};
        String[] dates = { "2015-03-06 14:33", "2015-03-06 14:24", "2015-03-06 09:30",
                           "2015-03-05 22:12", "2015-03-04 12:33", "2015-03-04 11:12",
                           "2015-03-03 10:10", "2015-03-03 04:10", "2015-03-02 14:33"};
        mDataset = new ArrayList<>();
        for(int i = 0; i < DATASET_COUNT; i++) {
            ReplyInfo replyInfo = new ReplyInfo(usernames[i]);
            replyInfo.setDate(dates[i]);
            replyInfo.setReplied(true);
            mDataset.add(replyInfo);
        }

        LoadImagesTask asyncTask = new LoadImagesTask();
        this.asyncTaskWeakRef = new WeakReference<>(asyncTask);
        asyncTask.execute(DATASET_COUNT);
    }


    private class LoadImagesTask extends AsyncTask<Integer, Void, Void> {


        private LoadImagesTask() {
        }

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), "Wait", "Retrieving conversations...");
        }

        @Override
        protected Void doInBackground(Integer... params) {
            for (int i = 0; i < params[0]; i++) {
                // Load and scale images
                BitmapDecoder bitmapDecoder = new BitmapDecoder(getActivity());
                Bitmap decodedImage = BitmapDecoder.decodeFile(getResources(), fullImages[i]);
                Bitmap thumbImage = ThumbnailUtils.extractThumbnail(decodedImage, bitmapDecoder.getScreenWidth(), 200);
                Bitmap blurredThumbImage = BitmapDecoder.blurBitmap(thumbImage, getActivity());
                Bitmap decodedProfile = BitmapDecoder.decodeSampledBitmapFromResource(getResources(), profilePictures[i],
                        50, 50);

                mDataset.get(i).setImage(decodedImage);
                mDataset.get(i).setThumbnail(blurredThumbImage);
                mDataset.get(i).setProfilePicture(decodedProfile);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            mAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }
    }

}
