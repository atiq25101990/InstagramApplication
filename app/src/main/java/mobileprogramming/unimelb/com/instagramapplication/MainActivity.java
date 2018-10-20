package mobileprogramming.unimelb.com.instagramapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobileprogramming.unimelb.com.instagramapplication.ActivityFeed.ActivityFeed;
import mobileprogramming.unimelb.com.instagramapplication.Share.PhotoFragment;
import mobileprogramming.unimelb.com.instagramapplication.Share.ShareActivity;
import mobileprogramming.unimelb.com.instagramapplication.utils.CommonUtils;
import mobileprogramming.unimelb.com.instagramapplication.utils.Constant;
import mobileprogramming.unimelb.com.instagramapplication.utils.SessionManagers;
import mobileprogramming.unimelb.com.instagramapplication.utils.UniversalImageLoader;

public class MainActivity extends AppCompatActivity {
    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private UserFeedsFragment userFeedsFragment;
    private DiscoverFragment discoverFragment;
    private PhotoFragment photoFragment;
    private ProfileFragment profileFragment;
    private FirebaseFirestore mFirestore;
    private String user_id;
    private static final String TAG = "MainActivity";
    private String MAC;


    @Override
    protected void onResume() {
        super.onResume();
        setBottomNavigationView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CommonUtils.showLoadingDialog(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        MAC = getMacAddr();

        getSupportActionBar().setTitle("Instagram");
        // checking gi t ,erge

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        userFeedsFragment = UserFeedsFragment.newInstance();
        discoverFragment = new DiscoverFragment();
        profileFragment = ProfileFragment.newInstance();

        if (!SessionManagers.getInstance().IsLogin()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {

                user_id = mAuth.getCurrentUser().getUid();
                mFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                HashMap<String, String> userProfile = new HashMap<>();
                                userProfile.put(Constant.KEY_USERID, "--");
                                userProfile.put(Constant.KEY_EMAIL, "--");
                                userProfile.put(Constant.KEY_NAME, task.getResult().getString("name"));
                                userProfile.put(Constant.KEY_UNAME, task.getResult().getString("username"));
                                SessionManagers.getInstance().setUserProfile(userProfile);
                                setFragment(userFeedsFragment);
                                selectFragment();
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "Something went wrong ! please try again !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            //Intent shareActivityIntent = new Intent(MainActivity.this, FriendsNearby.class);
            //startActivity(shareActivityIntent);
            setFragment(userFeedsFragment);
            selectFragment();
        }

        CommonUtils.dismissProgressDialog();
        updateMac();
        initImageLoader();
    }

    private void updateMac() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, String> macHash = new HashMap<>();
        macHash.put("MAC", MAC);
        db.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update("MAC", MAC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: UpdatedMAC");
                    }
                });
    }


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            Log.d(TAG, "getMacAddr: could not get MAC");
        }
        return "02:00:00:00:00:00";
    }

    private void selectFragment() {
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.nav_userfeeds:
                        setFragment(userFeedsFragment);
                        return true;

                    case R.id.nav_discover:
                        setFragment(discoverFragment);
                        return true;

                    case R.id.nav_photo:
                        //setFragment(photoFragment);
                        Intent shareActivityIntent = new Intent(MainActivity.this, ShareActivity.class);
                        shareActivityIntent.putExtra("type", "post");
                        startActivity(shareActivityIntent);
                        return true;

                    case R.id.nav_activityfeeds:
                        Intent activityFeedIntent = new Intent(MainActivity.this, ActivityFeed.class);
                        startActivity(activityFeedIntent);
                        return true;

                    case R.id.nav_profile:
                        setFragment(profileFragment);
                        return true;

                    default:
                        return false;
                }


            }
        });
    }

    private void setFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.userfeeds_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent settingsIntent;

        switch (item.getItemId()) {
            case R.id.action_logout_btn:

                logout();
                SessionManagers.getInstance().logoutUser(MainActivity.this);
                return true;

            case R.id.action_settings_btn:
                //account setting button is clicked
                settingsIntent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(settingsIntent);
                return true;

            case R.id.action_bluetooth_btn:
                //account setting button is clicked
                settingsIntent = new Intent(MainActivity.this, FriendsNearby.class);
                startActivity(settingsIntent);
                Toast.makeText(MainActivity.this, "Initiating Bluetooth Activity!", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return false;
        }
    }

    /**
     * Exit the app if back button is pressed on the home tab.
     * Otherwise go to the previous tab.
     */
    @Override
    public void onBackPressed() {
        Fragment fragment = getVisibleFragment();
        if (fragment instanceof UserFeedsFragment) {
            finish();
        } else {
            super.onBackPressed();
        }
        setBottomNavigationView();
    }

    public void setBottomNavigationView() {
        Fragment fragment = getVisibleFragment();
        if (fragment != null) {
            if (fragment instanceof UserFeedsFragment && mMainNav.getSelectedItemId() != R.id.nav_userfeeds) {
                mMainNav.setSelectedItemId(R.id.nav_userfeeds);
            } else if (fragment instanceof DiscoverFragment && mMainNav.getSelectedItemId() != R.id.nav_discover) {
                mMainNav.setSelectedItemId(R.id.nav_discover);
            } else if (fragment instanceof ProfileFragment && mMainNav.getSelectedItemId() != R.id.nav_profile) {
                mMainNav.setSelectedItemId(R.id.nav_profile);
            }
        }
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    private void logout() {

        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        // No user is signed in
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

}