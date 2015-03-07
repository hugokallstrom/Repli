package huka.com.repli;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;


public class ViewReplyActivity extends Activity {

    protected static final int CAPTURE_IMAGE_REQUEST_CODE = 1;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reply);
        setBackgroundPicture();
        setImageFileDir();
        Button button = (Button) findViewById(R.id.replyButton);
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
        byte[] b = extras.getByteArray("picture");

        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        ImageView image = (ImageView) findViewById(R.id.replyFullImageView);
        image.setImageBitmap(bmp);
    }

    private void setImageFileDir() {
        File dir = ViewReplyActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(dir+"/"+"reply.jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            // send to server so that new list in RecyclerViewFragment
            // displays "Waiting for reply"
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
