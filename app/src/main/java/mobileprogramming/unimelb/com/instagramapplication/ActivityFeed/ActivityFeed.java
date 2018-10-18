package mobileprogramming.unimelb.com.instagramapplication.ActivityFeed;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.adapter.ActivityFeedViewPagerAdapter;

public class ActivityFeed extends FragmentActivity {

    private ViewPager mPager;
    private static final String TAG = "ActivityFeed";
    private ActivityFeedViewPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.activityFeedViewPager);
        mPagerAdapter = new ActivityFeedViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new FollowerFragment());
        mPagerAdapter.addFragment(new FollowingFragment());
        mPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.activityFeedTabs);
        tabLayout.setupWithViewPager(mPager);
        Log.d(TAG, "onCreate: Setup Done");





    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
}
