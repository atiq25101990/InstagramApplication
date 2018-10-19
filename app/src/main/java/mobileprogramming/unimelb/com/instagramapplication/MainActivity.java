package mobileprogramming.unimelb.com.instagramapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import mobileprogramming.unimelb.com.instagramapplication.ActivityFeed.ActivityFeed;
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
    private final UserFeedsFragment userFeedsFragment = new UserFeedsFragment();
    private DiscoverFragment discoverFragment = new DiscoverFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private FirebaseFirestore mFirestore;
    private String user_id;
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = userFeedsFragment;



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

        getSupportActionBar().setTitle("Instagram");
        // checking gi t ,erge

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

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
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "Something went wrong ! please try again !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }


        Bundle bundle = new Bundle();
        bundle.putString("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        userFeedsFragment.setArguments(bundle);
        profileFragment.setArguments(bundle);
        discoverFragment.setArguments(bundle);


        fm.beginTransaction().add(R.id.main_frame, discoverFragment, discoverFragment.getTAG()).hide(discoverFragment).commit();
        fm.beginTransaction().add(R.id.main_frame, profileFragment, profileFragment.getTAG()).hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.main_frame, userFeedsFragment, userFeedsFragment.getTAG()).commit();

        setFragment(userFeedsFragment);
        selectFragment();

        CommonUtils.dismissProgressDialog();
        initImageLoader();
    }


    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
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
        fm.beginTransaction().hide(active).show(fragment).commit();
        active = fragment;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.userfeeds_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout_btn:

                logout();
                SessionManagers.getInstance().logoutUser(MainActivity.this);
                return true;

            case R.id.action_settings_btn:
                //account setting button is clicked
                Intent settingsIntent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(settingsIntent);
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
        if (active instanceof UserFeedsFragment) {
            finish();
        } else {
            setFragment(userFeedsFragment);
            setBottomNavigationView();
        }
    }

    public void setBottomNavigationView() {

        if (active != null) {
            if (active instanceof UserFeedsFragment && mMainNav.getSelectedItemId() != R.id.nav_userfeeds) {
                mMainNav.setSelectedItemId(R.id.nav_userfeeds);
            } else if (active instanceof DiscoverFragment && mMainNav.getSelectedItemId() != R.id.nav_discover) {
                mMainNav.setSelectedItemId(R.id.nav_discover);
            } else if (active instanceof ProfileFragment && mMainNav.getSelectedItemId() != R.id.nav_profile) {
                mMainNav.setSelectedItemId(R.id.nav_profile);
            }
        }
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