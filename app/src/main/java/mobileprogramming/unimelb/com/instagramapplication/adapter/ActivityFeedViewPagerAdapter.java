package mobileprogramming.unimelb.com.instagramapplication.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import mobileprogramming.unimelb.com.instagramapplication.ActivityFeed.FollowerFragment;
import mobileprogramming.unimelb.com.instagramapplication.ActivityFeed.FollowingFragment;

public class ActivityFeedViewPagerAdapter extends FragmentPagerAdapter {

    final static int NUM_PAGES = 2;

    private final List<Fragment> mFragmentList = new ArrayList<>();


    public ActivityFeedViewPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "YOU";
            case 1:
                return "FOLLOWERS";
            default:
                return "YOU";
        }
    }

    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }
}
