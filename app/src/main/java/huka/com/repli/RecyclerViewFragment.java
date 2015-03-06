package huka.com.repli;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import adapters.MyRecyclerAdapter;
import views.RoundedImageView;


public class RecyclerViewFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int DATASET_COUNT = 9;

    //TO-DO REMOVE
    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected MyRecyclerAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<ReplyInfo> mDataset;
    protected Context mContext;
    private int[] thumbnails;
    private int[] profilePictures;
    private int[] fullImages;

    public static RecyclerViewFragment newInstance() {
        return new RecyclerViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thumbnails = new int[] {R.drawable.thumbnail1, R.drawable.thumbnail2, R.drawable.thumbnail3,
                R.drawable.thumbnail4, R.drawable.thumbnail5, R.drawable.thumbnail6,
                R.drawable.thumbnail7, R.drawable.thumbnail8, R.drawable.thumbnail9};
        profilePictures = new int[] {R.drawable.profile_picture1, R.drawable.profile_picture2, R.drawable.profile_picture3,
                R.drawable.profile_picture4, R.drawable.profile_picture5, R.drawable.profile_picture6,
                R.drawable.profile_picture7, R.drawable.profile_picture8, R.drawable.profile_picture9};
        fullImages = new int[] {R.drawable.image1};
        initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
        rootView.setTag(TAG);

        mContext = container.getContext();
        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
       // setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyRecyclerAdapter(mDataset, this);
        // Set Adapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // END_INCLUDE(initializeRecyclerView)

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {
        String[] usernames = { "Danne", "Schött", "Jojje", "Limpa", "Hinners",
                               "Hiltan", "Macke", "Suther", "Bophin"};
        String[] dates = { "2015-03-06 14:33", "2015-03-06 14:24", "2015-03-06 09:30",
                           "2015-03-05 22:12", "2015-03-04 12:33", "2015-03-04 11:12",
                           "2015-03-03 10:10", "2015-03-03 04:10", "2015-03-02 14:33"};

        mDataset = new ArrayList<>();
        for(int i = 0; i < DATASET_COUNT; i++) {
            Bitmap profilepic = RoundedImageView.getCroppedBitmap(BitmapFactory.decodeResource(getResources(), profilePictures[i]), 50);
            Drawable drawableProfilepic = new BitmapDrawable(getResources(), profilepic);

            ReplyInfo replyInfo = new ReplyInfo(usernames[i]);
            replyInfo.setDate(dates[i]);
            replyInfo.setReplied(true);
            replyInfo.setProfilePicture(drawableProfilepic);
            replyInfo.setThumbnail(getResources().getDrawable(thumbnails[i]));
            replyInfo.setReplied(true);
            mDataset.add(replyInfo);
        }

        mDataset.get(4).setReplied(false);
        mDataset.get(6).setReplied(false);
        mDataset.get(7).setReplied(false);
        mDataset.get(8).setReplied(false);
    }
}
