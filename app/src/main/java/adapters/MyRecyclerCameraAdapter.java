package adapters;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import huka.com.repli.R;
import huka.com.repli.ReplyInfo;

public class MyRecyclerCameraAdapter extends RecyclerView.Adapter<MyRecyclerCameraAdapter.ViewHolder> {

    private ArrayList<Bitmap> mDataSet;
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
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public MyRecyclerCameraAdapter(ArrayList<Bitmap> dataSet) {
        super();
        mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.camera_row_item, viewGroup, false);
        return new ViewHolder(v);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Bitmap image = mDataSet.get(position);
        viewHolder.getRandomImage().setImageBitmap(image);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}

