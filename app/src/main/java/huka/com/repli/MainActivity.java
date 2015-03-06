package huka.com.repli;

import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import adapters.MyFragmentPagerAdapter;
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
}
