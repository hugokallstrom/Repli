package adapters;


        import android.content.Context;
        import android.util.DisplayMetrics;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import huka.com.repli.MainActivity;
        import huka.com.repli.R;


public class CameraAdapter extends BaseAdapter {
    private Context context;


    public CameraAdapter(Context context) {
        this.context = context;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);


            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.camera_gridview_item, null);
            //gridView.setMinimumHeight(MainActivity.SCREEN_HEIGHT/2);
            System.out.println(MainActivity.SCREEN_HEIGHT);
            // set value into textview


            // set image based on selected text
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.grid_item_image);


                imageView.setImageResource(R.drawable.user_profile_picture);


        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}