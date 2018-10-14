package mobileprogramming.unimelb.com.instagramapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import mobileprogramming.unimelb.com.instagramapplication.utils.CommonUtils;

public class MainActivity extends AppCompatActivity {

    public static String username;
    public static String name;
    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;
    private UserFeedsFragment userFeedsFragment;
    private DiscoverFragment discoverFragment;
    private PhotoFragment photoFragment;
    private ActivityFeedsFragment activityFeedsFragment;
    private ProfileFragment profileFragment;
    private FirebaseFirestore mFirestore;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
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


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            CommonUtils.showLoadingDialog(MainActivity.this);
            user_id = mAuth.getCurrentUser().getUid();
            mFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    CommonUtils.dismissProgressDialog();
                    if (task.isSuccessful()) {
                        //setting a default fragment
                        setFragment(userFeedsFragment);
                        selectFragment();
                        if (task.getResult().exists()) {
                            String image = task.getResult().getString("image");
                            name = task.getResult().getString("name");
                            username = task.getResult().getString("username");
                            String bio = task.getResult().getString("bio");
                        }

                    }
                }
            });
        }
    }

    private void selectFragment() {
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

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

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
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
