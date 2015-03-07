package huka.com.repli;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import adapters.MyRecyclerReplyAdapter;
import views.RoundedImageView;


public class RepliesFragment extends android.support.v4.app.Fragment {

    private static final int DATASET_COUNT = 9;

    protected RecyclerView mRecyclerView;
    protected MyRecyclerReplyAdapter mAdapter;
    protected ArrayList<ReplyInfo> mDataset;
    FragmentActivity mActivity;
    private int[] thumbnails;
    private int[] profilePictures;
    private int[] fullImages;

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
        thumbnails = new int[] {R.drawable.thumbnail1, R.drawable.thumbnail2, R.drawable.thumbnail3,
                R.drawable.thumbnail4, R.drawable.thumbnail5, R.drawable.thumbnail6,
                R.drawable.thumbnail7, R.drawable.thumbnail8, R.drawable.thumbnail9};
        profilePictures = new int[] {R.drawable.profile_picture1, R.drawable.profile_picture2, R.drawable.profile_picture3,
                R.drawable.profile_picture4, R.drawable.profile_picture5, R.drawable.profile_picture6,
                R.drawable.profile_picture7, R.drawable.profile_picture8, R.drawable.profile_picture9};
        fullImages = new int[] {R.drawable.image1, R.drawable.image2, R.drawable.image3,
                R.drawable.image4, R.drawable.image5, R.drawable.image6, R.drawable.image7, R.drawable.image8,
                R.drawable.image9};
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

                BitmapDecoder bitmapDecoder = new BitmapDecoder(getActivity());
                Bitmap decodedImage = BitmapDecoder.decodeSampledBitmapFromResource(getResources(), fullImages[position],
                        bitmapDecoder.getScreenWidth(), bitmapDecoder.getScreenHeight());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                decodedImage.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                byte[] b = baos.toByteArray();

                Intent intent = new Intent(getActivity(), ViewReplyActivity.class);
                intent.putExtra("picture", b);
                startActivity(intent);

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
