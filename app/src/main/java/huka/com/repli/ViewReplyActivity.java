package huka.com.repli;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;

import servercalls.SendReplyAsyncTask;
import servercalls.UploadProfilePicAsyncTask;

/**
 * Used when viewing a picture from either of the
 * two tabs.
 */
public class ViewReplyActivity extends Activity {

    protected static final int CAPTURE_IMAGE_REQUEST_CODE = 1;
    private File file;
    private String accountName;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reply);
        image = (ImageView) findViewById(R.id.replyFullImageView);
        this.getActionBar().hide();
        setBackgroundPicture();
        setImageFileDir();
        Button button = (Button) findViewById(R.id.replyButton);
        accountName = (String) getIntent().getExtras().getCharSequence("accountName");
        button.getBackground().setAlpha(160);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                ViewReplyActivity.this.startActivityForResult(imageIntent, CAPTURE_IMAGE_REQUEST_CODE);
            }
        });
    }

    private void setBackgroundPicture() {
        Bundle extras = getIntent().getExtras();
        String pictureUrl = extras.getString("picture");
        new DownloadImageTask(image).execute(pictureUrl);
    }

    private void setImageFileDir() {
        File dir = ViewReplyActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(dir+"/"+"reply.jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode != 0) {
            Intent intent = new Intent(this, MainActivity.class);
            new SendReplyAsyncTask(this).execute(file.getAbsolutePath(), accountName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
