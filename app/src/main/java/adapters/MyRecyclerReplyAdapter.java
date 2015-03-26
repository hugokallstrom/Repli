package adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import huka.com.repli.R;
import huka.com.repli.ReplyInfo;
import servercalls.RemoveReplyAsyncTask;

/**
 * Adapter for ReplyFragment.
 */
public class MyRecyclerReplyAdapter extends RecyclerView.Adapter<MyRecyclerReplyAdapter.ViewHolder> {

    private ArrayList<ReplyInfo> mDataSet;
    private OnItemClickListener mItemClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final ImageView thumbnailView;
        private final CircleImageView profilePictureView;
        private final TextView usernameText;
        private final TextView dateText;
        private final int inactiveColor;
        private final int activeColor;

        public ViewHolder(View v) {
            super(v);
            usernameText = (TextView) v.findViewById(R.id.usernameText);
            dateText = (TextView) v.findViewById(R.id.dateText);
            thumbnailView = (ImageView) v.findViewById(R.id.thumbnailView);
            profilePictureView = (CircleImageView) v.findViewById(R.id.profilePicture);
            inactiveColor = v.getResources().getColor(R.color.white);
            activeColor = v.getResources().getColor(R.color.primary_dark);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        public TextView getUsernameText() {
            return usernameText;
        }
        public TextView getDateTxt() {
            return dateText;
        }
        public ImageView getThumbnailView() {
            return thumbnailView;
        }
        public CircleImageView getProfilePictureView() {
            return profilePictureView;
        }
        public int getInactiveColor() {
            return inactiveColor;
        }
        public int getActiveColor() {
            return activeColor;
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemClickListener != null) {
                return mItemClickListener.onItemLongClicked(getPosition());
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
        public boolean onItemLongClicked(int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    /**
     * Initialize the dataset of the Adapter.
     */
    public MyRecyclerReplyAdapter(ArrayList<ReplyInfo> dataSet) {
        super();
        mDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.reply_row_item, viewGroup, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ReplyInfo replyInfo = mDataSet.get(position);
        int bordercolor;
        // Build the view
       // new GetImageAsyncTask(viewHolder).execute(replyInfo.getProfilePicture(), replyInfo.getImage());

        viewHolder.getUsernameText().setText(replyInfo.getUsername());
        viewHolder.getDateTxt().setText(replyInfo.getDate());
        viewHolder.getThumbnailView().setColorFilter(R.color.primary);

        if(!replyInfo.isReplied()) {
            bordercolor = viewHolder.getInactiveColor();
        } else {
            bordercolor = viewHolder.getActiveColor();
        }
        viewHolder.getProfilePictureView().setBorderColor(bordercolor);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void removeItem(int position) {
        new RemoveReplyAsyncTask().execute(mDataSet.get(position));
        mDataSet.remove(position);
        notifyItemRemoved(position);
    }

    public void setDataSet(ArrayList<ReplyInfo> newDataSet){
        mDataSet = newDataSet;
        notifyDataSetChanged();
    }

    private class GetImageAsyncTask extends AsyncTask<String, Void, Void> {

        private ViewHolder viewHolder;

        public GetImageAsyncTask(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }
        @Override
        protected Void doInBackground(String... params) {
            String profilePictureUrl = params[0];
            String thumbNailUrl = params[1];
            Bitmap profilePicture = getBitmapFromURL(profilePictureUrl);
            Bitmap thumbNail = getBitmapFromURL(thumbNailUrl);

            viewHolder.getThumbnailView().setImageBitmap(thumbNail);
            viewHolder.getProfilePictureView().setImageBitmap(profilePicture);
            return null;
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

