package mobileprogramming.unimelb.com.instagramapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class SetupActivity extends AppCompatActivity {

    //CircleImageView is a custom image attribute obtained
    //through GitHub from author hdodenhof to get rounded shaped image
    private CircleImageView setupImage;
    //Once again we use custom library to do the job of cropping of images
    //(Android Image Cropper)obtained through GitHub from author theartofdev
    //specifically this variable job is to constantly check for update in cropped image
    private Uri mainImageURI = null;

    //variables
    private String user_id;
    private boolean isChanged = false;

    //frontend variables
    private EditText setupName;
    private EditText setupUsername;
    private EditText setupBio;
    private Button setupBtn;
    private ProgressBar setupProgress;

    //Firebase variables
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    //location variable
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        final Toolbar setupToolbar = findViewById(R.id.setup_toolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name);
        setupUsername = findViewById(R.id.setup_username);
        setupBio = findViewById(R.id.setup_bio);
        setupBtn = findViewById(R.id.setup_btn);
        setupProgress = findViewById(R.id.setup_progress);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //For user data retrival user_id is necessity
        user_id = mAuth.getCurrentUser().getUid();

        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);

        mFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    if(task.getResult().exists()){
                        //Data exist
                        //Toast.makeText(SetupActivity.this, "Data exist", Toast.LENGTH_LONG).show();
                        String image = task.getResult().getString("image");
                        String name = task.getResult().getString("name");
                        String username = task.getResult().getString("username");
                        String bio = task.getResult().getString("bio");
                        mainImageURI = Uri.parse(image);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_profile_picture);
                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);

                        setupName.setText(name);
                        setupUsername.setText(username);
                        setupBio.setText(bio);



                    }else{
                        //Data does not exist
                        //Toast.makeText(SetupActivity.this, "Data does not exist", Toast.LENGTH_LONG).show();
                    }

                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "Firestore Retrieval Error"+ error, Toast.LENGTH_LONG).show();
                }

                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }
        });

        setupBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                final String name = setupName.getText().toString();
                final String username =setupUsername.getText().toString();
                final String bio = setupBio.getText().toString();

                if(!TextUtils.isEmpty(username) && mainImageURI != null) {

                    setupProgress.setVisibility(View.VISIBLE);

                    if(isChanged){

                        user_id = mAuth.getCurrentUser().getUid();

                        final StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");

                        image_path.putFile(mainImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    storeFirestore(taskSnapshot, uri, name, username, bio);
                                }


                            });
                            setupProgress.setVisibility(View.INVISIBLE);
                            }
                        });


                    }else{

                        storeFirestore(null, mainImageURI, name, username, bio);

                    }//end of isChanged if statement

                } //end of validating username if statement

            }//end of onclick method


        });


        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Version greater than Marshmellow
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(SetupActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(SetupActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetupActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }else{

                        BringImagePicker();

                    }

                }else{

                    BringImagePicker();

                }

            }
        });
    }

    private void storeFirestore(UploadTask.TaskSnapshot taskSnapshot, Uri uri, String name, String username, String bio) {

        String downloadUri;

        if(taskSnapshot!=null){

            downloadUri = uri.toString();
            Log.d("", "onSuccess: uri= "+ downloadUri);

        }else{
            downloadUri = mainImageURI.toString();
        }

        Map<String,String> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("username", username);
        userMap.put("bio", bio);
        userMap.put("image", downloadUri);

        mFirestore.collection("Users").document(user_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(SetupActivity.this, "User settings are updated", Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
                setupProgress.setVisibility(View.INVISIBLE);
            }
        });

    }


    private void BringImagePicker() {

        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);
                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }
}
