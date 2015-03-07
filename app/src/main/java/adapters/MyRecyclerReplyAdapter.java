package adapters;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import huka.com.repli.R;
import huka.com.repli.ReplyInfo;

public class MyRecyclerReplyAdapter extends RecyclerView.Adapter<MyRecyclerReplyAdapter.ViewHolder> {

    private ArrayList<ReplyInfo> mDataSet;
    private OnItemClickListener mItemClickListener;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView usernameText;
        private final ImageView thumbnailView;
        private final TextView dateText;
        private final ImageView profilePictureView;
        private final TextView waitingText;

        public ViewHolder(View v) {
            super(v);
            usernameText = (TextView) v.findViewById(R.id.usernameText);
            dateText = (TextView) v.findViewById(R.id.dateText);
            waitingText = (TextView) v.findViewById(R.id.waitingText);
            thumbnailView = (ImageView) v.findViewById(R.id.thumbnailView);
            profilePictureView = (ImageView) v.findViewById(R.id.profilePicture);
            v.setOnClickListener(this);
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

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public MyRecyclerReplyAdapter(ArrayList<ReplyInfo> dataSet) {
        super();
        mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.reply_row_item, viewGroup, false);
        return new ViewHolder(v);

    }

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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}

