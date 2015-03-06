package adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import huka.com.repli.PageFragment;
import huka.com.repli.RecyclerViewFragment;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[] { "Chat", "Camera" };
    private Context context;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: return RecyclerViewFragment.newInstance();
            case 1: return PageFragment.newInstance(position +1);
        }
        return PageFragment.newInstance(position + 1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
