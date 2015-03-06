package adapters;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import huka.com.repli.R;
import huka.com.repli.RecyclerViewFragment;
import huka.com.repli.ReplyInfo;
import huka.com.repli.ViewReplyActivity;

/**
 * Created by hugo on 3/5/15.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private final Fragment mFragment;
    private ArrayList<ReplyInfo> mDataSet;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameText;
        private final ImageView thumbnailView;
        private final TextView dateText;
        private final ImageView profilePictureView;
        private final TextView waitingText;
        private final RelativeLayout activity_view_reply;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("Clicked", "Clicked on pos: " + getPosition());

                }
            });

            usernameText = (TextView) v.findViewById(R.id.usernameText);
            dateText = (TextView) v.findViewById(R.id.dateText);
            waitingText = (TextView) v.findViewById(R.id.waitingText);
            thumbnailView = (ImageView) v.findViewById(R.id.thumbnailView);
            profilePictureView = (ImageView) v.findViewById(R.id.profilePicture);
            activity_view_reply = (RelativeLayout) v.findViewById(R.id.activity_view_reply);
        }

        public TextView getUsernameText() {
            return usernameText;
        }
        public TextView getDateTxt() {
            return dateText;
        }
        public TextView getWaitingText() {
            return waitingText;
        }
        public ImageView getThumbnailView() {
            return thumbnailView;
        }
        public ImageView getProfilePictureView() {
            return profilePictureView;
        }
        public RelativeLayout getActivity_view_reply() {
            return activity_view_reply;
        }

    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public MyRecyclerAdapter(ArrayList<ReplyInfo> dataSet, Fragment fragment) {
        mDataSet = dataSet;
        mFragment = fragment;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);
        return new ViewHolder(v);

    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ReplyInfo replyInfo = mDataSet.get(position);

        viewHolder.getUsernameText().setText(replyInfo.getUsername());
        viewHolder.getDateTxt().setText(replyInfo.getDate());
        viewHolder.getThumbnailView().setBackground(replyInfo.getThumbnail());
        viewHolder.getProfilePictureView().setBackground(replyInfo.getProfilePicture());

        if(!replyInfo.isReplied()) {
            makeBlackAndWhite(replyInfo.getThumbnail());
            viewHolder.getWaitingText().setText(R.string.waitingText);
        }
    }

    private Drawable makeBlackAndWhite(Drawable drawable) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        drawable.setColorFilter(filter);
        return drawable;
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}

