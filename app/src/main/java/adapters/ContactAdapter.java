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

import huka.com.repli.R;
import huka.com.repli.ReplyInfo;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private ArrayList<ReplyInfo> mDataSet;
    private OnItemClickListener mItemClickListener;

    public ContactAdapter() {

    }

    public void removeItem(int itemPosition) {
        mDataSet.remove(itemPosition);
        notifyDataSetChanged();
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final ImageView randomImage;

        public ContactViewHolder(View v) {
            super(v);
            randomImage = (ImageView) v.findViewById(R.id.cardImage);
            v.setOnClickListener(this);
        }

        public ImageView getRandomImage() {
            return randomImage;
        }

        @Override
        public void onClick(View v) {
            System.out.println("CLICKED item");

            if (mItemClickListener != null) {
                System.out.println("THIS ITEM + " + getPosition());
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
    public ContactAdapter(ArrayList<ReplyInfo> dataSet) {
        super();
        mDataSet = dataSet;
        System.out.println("-----------------------" + mDataSet.size());
        notifyDataSetChanged();
    }

    public void setmDataSet(ArrayList<ReplyInfo> mDataSet) {
        this.mDataSet = mDataSet;
        notifyDataSetChanged();

    }

    public ArrayList<ReplyInfo> getmDataSet() {
        return mDataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.random_images_cardview, viewGroup, false);
        return new ContactViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ContactViewHolder viewHolder, final int position) {
        String imageUrl = mDataSet.get(position).getThumbnail();
        new DownloadImageTask(viewHolder.getRandomImage()).execute(imageUrl, String.valueOf(position));
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
            String position = urls[1];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
                mDataSet.get(Integer.valueOf(position)).setBitmapImage(bitmap);
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


