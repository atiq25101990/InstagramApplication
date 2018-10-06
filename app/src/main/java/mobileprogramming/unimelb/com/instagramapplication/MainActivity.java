package mobileprogramming.unimelb.com.instagramapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private UserFeedsFragment userFeedsFragment;
    private DiscoverFragment discoverFragment;
    private PhotoFragment photoFragment;
    private ActivityFeedsFragment activityFeedsFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setTitle("Instagram");

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        userFeedsFragment = new UserFeedsFragment();
        discoverFragment = new DiscoverFragment();
        photoFragment = new PhotoFragment();
        activityFeedsFragment = new ActivityFeedsFragment();
        profileFragment = new ProfileFragment();

        //setting a default fragment
        setFragment(userFeedsFragment);
        //selecting a fragment
        selectFragment();

    }

    private void selectFragment() {
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){

                    case R.id.nav_userfeeds:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(userFeedsFragment);
                        return true;

                    case R.id.nav_discover:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(discoverFragment);
                        return true;

                    case R.id.nav_photo:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(photoFragment);
                        return true;

                    case R.id.nav_activityfeeds:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(activityFeedsFragment);
                        return true;

                    case R.id.nav_profile:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(profileFragment);
                        return true;

                    default:
                        return false;
                }

            }
        });
    }

    private void setFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Refer google docs Authentication >> Android >> Manage Users
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){

            sendToLogin();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.userfeeds_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_logout_btn:
                //logout button is clicked
                logout();
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
