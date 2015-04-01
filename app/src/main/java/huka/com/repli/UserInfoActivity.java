package huka.com.repli;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import servercalls.UploadProfilePicAsyncTask;

/**
 * Displays info about the current user; user name, email and profilepicture.
 */
public class UserInfoActivity extends Activity {

    protected static final int CAPTURE_IMAGE_REQUEST_CODE = 100;
    private SharedPreferences sharedPreferences;
    private TextView usernameText;
    private TextView emailText;
    private CircleImageView profilePicture;
    private Button changePictureButton;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_user_info);
        setImageFileDir();
        usernameText = (TextView) findViewById(R.id.userinfo_usernameText);
        emailText = (TextView) findViewById(R.id.userinfo_emailText);
        profilePicture = (CircleImageView) findViewById(R.id.userinfo_picture);
        changePictureButton = (Button) findViewById(R.id.userinfo_changepictureButton);
        setUsername();
        setProfilePicture();
    }

    private void setUsername() {
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(LoginActivity.ACCOUNT_NAME, "none");
        String email = sharedPreferences.getString(LoginActivity.EMAIL, "none");
        usernameText.setText(name);
        emailText.setText(email);
    }

    private void setProfilePicture() {
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        String profilePicUrl = sharedPreferences.getString(LoginActivity.PROF_PIC, "none");
        if(profilePicUrl.equals("none")) {
            profilePicture.setImageResource(R.drawable.user_profile_picture);
        } else {
            new DownloadImageTask().execute(profilePicUrl);
        }
    }

    private void setImageFileDir() {
        File dir = UserInfoActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(dir+"/"+"profilePic.jpg");
    }

    public void changePictureListener(View v) {
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Uri.fromFile(file);
        Log.v("file: ", "uri: " + uri.getPath());
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(imageIntent, CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            profilePicture.setImageURI(null);
            profilePicture.setImageURI(Uri.fromFile(file));

            SharedPreferences.Editor spEditor = sharedPreferences.edit();
            spEditor.putString("PROFILE_PICTURE", Uri.fromFile(file).getPath()).apply();
            new UploadProfilePicAsyncTask(this).execute(file);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            DialogHandler.logoutDialog(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String profilePicUrl = urls[0];
            Bitmap bitmap = null;

            try {
                InputStream in = new java.net.URL(profilePicUrl).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            profilePicture.setImageBitmap(result);
        }
    }


}
