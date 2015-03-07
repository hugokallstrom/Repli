package huka.com.repli;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;

import adapters.MyFragmentPagerAdapter;
import floatingactionbuttonbasic.FloatingActionButton;
import slidingtabs.SlidingTabLayout;

public class MainActivity extends FragmentActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_tab_layout);

        PagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this);

        // Disable elevation for actionbar
        getActionBar().setElevation(0);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        // Give the SlidingTabLayout the ViewPager
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        // Customize the tablayout
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.primary));
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.white));
        slidingTabLayout.setViewPager(viewPager);
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
