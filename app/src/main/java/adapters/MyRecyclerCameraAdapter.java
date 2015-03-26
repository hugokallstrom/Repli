package adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;

import huka.com.repli.BitmapDecoder;
import huka.com.repli.R;
import huka.com.repli.ReplyInfo;

/**
 * Adapter for the RecyclerView in CameraFragment.
 */
public class MyRecyclerCameraAdapter extends RecyclerView.Adapter<MyRecyclerCameraAdapter.ViewHolder> {

    private ArrayList<ReplyInfo> mDataSet;
    private OnItemClickListener mItemClickListener;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView randomImage;

        public ViewHolder(View v) {
            super(v);
            randomImage = (ImageView) v.findViewById(R.id.randomImage);
            v.setOnClickListener(this);
        }

        public ImageView getRandomImage() {
            return randomImage;
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    /**
     * Initialize the dataset of the Adapter.
     */
    public MyRecyclerCameraAdapter(ArrayList<ReplyInfo> dataSet) {
        super();
        mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.camera_row_item, viewGroup, false);
        return new ViewHolder(v);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String imageUrl = mDataSet.get(position).getThumbnail();
        new DownloadImageTask(viewHolder.getRandomImage()).execute(imageUrl);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

