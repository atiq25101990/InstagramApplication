package mobileprogramming.unimelb.com.instagramapplication.Share;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.utils.CommonUtils;
import mobileprogramming.unimelb.com.instagramapplication.utils.FirebaseMethods;
import mobileprogramming.unimelb.com.instagramapplication.utils.Permissions;
import mobileprogramming.unimelb.com.instagramapplication.utils.UniversalImageLoader;

public class NextActivity extends AppCompatActivity {

    public static final String TAG = "NextActivity";

    //constants
    private static final int VERIFY_PERMISSIONS_REQUEST = 11;

    //firebase variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //widgets
    private EditText mCaption;

    //vars
    private String mAppend = "file:/";
    private int imageCount;
    private String imgUrl;
    private Location currentLocation;
    private Intent intent;
    private Bitmap bitmap;

    //currentLocation variables
    private FusedLocationProviderClient mFusedLocationClient;
    private String type;
    private String[] inRange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        Intent thisIntent = getIntent();
        type = thisIntent.getStringExtra("type");

        assert type != null;
        if(type.equals("inrange")){
            inRange = intent.getStringArrayExtra("users");
        }

        if(checkPermissionsArray(Permissions.PERMISSIONS)){
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                if ( ContextCompat.checkSelfPermission( this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

                    ActivityCompat.requestPermissions( this,  new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},11);
                }

                 mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known currentLocation. In some rare situations this can be null.
                            currentLocation = location;
                        if (location != null) {
                            // Logic to handle currentLocation object
                            currentLocation = location;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }



        mFirebaseMethods = new FirebaseMethods(NextActivity.this);
        mCaption = findViewById(R.id.caption);

        setupFirebaseAuth();

        ImageView backArrow = findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: closing the activity");
                finish();
            }
        });

        TextView share = findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: navigating to the final share screen");
                CommonUtils.showLoadingDialog(NextActivity.this);
                //upload the image to firebase
                String caption = mCaption.getText().toString();

                if(intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgUrl, null, currentLocation.toString(), type, inRange);
                }else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    byte[] byteArray = intent.getByteArrayExtra(getString(R.string.selected_bitmap));
                    bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, null, bitmap, currentLocation.toString(), type, inRange);
                }

            }
        });

        setImage();
    }



    private void someMethod(){
        /*
        * Step 1: Create a data model for photos
        *
        * Step 2: Add properties to the Photo Objects (caption, date, imageUrl, photo_id, tags, user_id)
        *
        * Step 3: Count the number of photos that the user already has.
        *
        * Step 4: i.    Upload the photo to Firebase Storage
        *         ii.   insert into 'post' node
        *         iii.  If I get time, insert into 'user_photos' node
        * */
    }

    /**
     * gets the image url from the incoming intent and displays the chosen image
     * */
    private void setImage(){
        intent = getIntent();
        ImageView image = findViewById(R.id.imageShare);

        if(intent.hasExtra(getString(R.string.selected_image))){
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new image: " +imgUrl);
            UniversalImageLoader.setImage(imgUrl, image, null, mAppend);
        }else if(intent.hasExtra(getString(R.string.selected_bitmap))){
            byte[] byteArray = intent.getByteArrayExtra(getString(R.string.selected_bitmap));
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Log.d(TAG, "setImage: got new bitmap");
            image.setImageBitmap(bitmap);
        }

    }

      /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count: "+imageCount);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: "+imageCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /***** Requesting user permission for enabling location if user choose no initially *****/

    //Verify all the permissions passed to the array
    private void verifyPermissions(String[] permissions) {
        Log.d(TAG,"checkPermissionsArray: checking permissions array.");

        ActivityCompat.requestPermissions(NextActivity.this, permissions, VERIFY_PERMISSIONS_REQUEST);

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

        int permissionRequest = ActivityCompat.checkSelfPermission(NextActivity.this, permission);
        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " +permission);
            return false;
        }else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " +permission);
            return true;
        }
    }

}
