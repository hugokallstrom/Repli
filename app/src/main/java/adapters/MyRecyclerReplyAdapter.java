package adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import huka.com.repli.BitmapDecoder;
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

    public void updateItemInListToAnswerd(String username){
        for(ReplyInfo replies : mDataSet){
            if(replies.getUsername().equalsIgnoreCase(username)){
               replies.setReplied(false);

               notifyDataSetChanged();
               break;
            }
        }
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
        new DownloadImageTask(viewHolder.getThumbnailView(), true).execute(replyInfo.getImage(), String.valueOf(position));
        new DownloadImageTask(viewHolder.getProfilePictureView(), false).execute(replyInfo.getProfilePicture(), String.valueOf(position));
        viewHolder.getUsernameText().setText(replyInfo.getUsername());
        viewHolder.getDateTxt().setText(replyInfo.getDate());
        //viewHolder.getThumbnailView().setColorFilter(R.color.primary);

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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;
        Boolean setImage;

        public DownloadImageTask(ImageView bmImage, Boolean setImage) {
            this.bmImage = bmImage;
            this.setImage = setImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            String position = urls[1];
            int pos = Integer.valueOf(position);
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
                if(!mDataSet.get(pos).isReplied()) {
                    System.out.println("MAKING BITMAP BW");
                    bitmap = BitmapDecoder.makeBlackAndWhite(bitmap);
                }

                if(setImage) {
                    mDataSet.get(pos).setBitmapImage(bitmap);
                }

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

