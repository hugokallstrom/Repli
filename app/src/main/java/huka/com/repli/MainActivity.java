package huka.com.repli;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;

import adapters.MyFragmentPagerAdapter;
import floatingactionbuttonbasic.FloatingActionButton;
import slidingtabs.SlidingTabLayout;

public class MainActivity extends FragmentActivity {

    private PagerAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_tab_layout);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this);

        // Disable elevation for actionbar
        getActionBar().setElevation(0);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        // Give the SlidingTabLayout the ViewPager
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        // Customize the tablayout
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.primary));
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.white));
        slidingTabLayout.setViewPager(viewPager);
        setPrimaryFragment();

    }

    private void setPrimaryFragment() {
        Log.v("MainActivity", "Received intent");
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            String primaryFragment = bundle.getString("primary");
            Log.v("MainActivity", "extra: " + primaryFragment);
            if (primaryFragment == null) {
                return;
            } else if(primaryFragment.equals("chat")) {
                viewPager.setCurrentItem(0);
            } else if(primaryFragment.equals("camera")) {
                Log.v("MainActivity", "set to camera tab");
                viewPager.setCurrentItem(1);

            }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //setResult(RESULT_CLOSE_ALL);
                        finish();
                    }
                }).setNegativeButton("No", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
