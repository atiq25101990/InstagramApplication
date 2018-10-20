package mobileprogramming.unimelb.com.instagramapplication.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.utils.Permissions;
import mobileprogramming.unimelb.com.instagramapplication.utils.SectionsPagerAdapter;

public class ShareActivity extends AppCompatActivity {



    private static final String TAG = "ShareActivity";

    //constants
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private ViewPager mViewPager;
    private Context mContext = ShareActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        if(checkPermissionsArray(Permissions.PERMISSIONS)){
            setupViewPager();
        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }
    }

    /**
     * return the current tab number
     * 0 = GalleryFragment
     * 1 = PhotoFragment
     * **/
    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();
    }

    //Setup viewpager for managing the tabs
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        String type = getIntent().getStringExtra("type");

        Bundle bundle = new Bundle();
        bundle.putString("type", type);

        Fragment galleryFragment = new GalleryFragment();
        galleryFragment.setArguments(bundle);

        Fragment photoFragment = new PhotoFragment();
        photoFragment.setArguments(bundle);

        adapter.addFragment(galleryFragment);
        adapter.addFragment(photoFragment);
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));
    }

    public int getTask(){
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();
    }

    //Verify all the permissions passed to the array
    private void verifyPermissions(String[] permissions) {
        Log.d(TAG,"checkPermissionsArray: checking permissions array.");

        ActivityCompat.requestPermissions(ShareActivity.this, permissions, VERIFY_PERMISSIONS_REQUEST);

    }


    //Check an array of permissions
    public boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG,"checkPermissionsArray: checking permissions array.");

        for(int i=0; i<permissions.length;i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    //check single permission if it has been verified or not
    public boolean checkPermissions(String permission) {
        Log.d(TAG,"checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);
        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " +permission);
            return false;
        }else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " +permission);
            return true;
        }
    }
}
