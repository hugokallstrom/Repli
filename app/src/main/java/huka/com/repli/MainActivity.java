package huka.com.repli;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import adapters.MyFragmentPagerAdapter;
import slidingtabs.SlidingTabLayout;
import views.TypefaceSpan;

/**
 * Starts the viewPager which holds CameraFragment and RepliesFragment.
 * Also handles logout.
 */
public class MainActivity extends FragmentActivity {

    private PagerAdapter adapter;
    private ViewPager viewPager;
    private static boolean activityVisible;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.activityPaused();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_tab_layout);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this);

        // Disable elevation for actionbar
        getActionBar().setElevation(0);
        SpannableString s = new SpannableString("Repli");
        s.setSpan(new TypefaceSpan(this, "Pacifico.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        // Give the SlidingTabLayout the ViewPager
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        // Customize the SlidingTabLayout
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.white));
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.primary));
        slidingTabLayout.setElevation(10);
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

    /**
     * Logout on back pressed.
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("No", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent intent = new Intent(this, UserInfoActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_logout) {
            // Logout
            DialogHandler.logoutDialog(this);
        }

        return super.onOptionsItemSelected(item);
    }


}
