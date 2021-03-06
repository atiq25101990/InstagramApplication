package mobileprogramming.unimelb.com.instagramapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import mobileprogramming.unimelb.com.instagramapplication.MainActivity;
import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.models.InRangePhoto;
import mobileprogramming.unimelb.com.instagramapplication.models.Model;
import mobileprogramming.unimelb.com.instagramapplication.models.Photo;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;
    private FirebaseFirestore mFirestore;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;
    private String photoLocation;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;
        mFirestore = FirebaseFirestore.getInstance();

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void uploadNewPhoto(String photoType, final String caption, final int count, final String imgUrl, Bitmap bm,
                               final String location, final String type, final String[] inRange){

        Log.d(TAG, "uploadNewPhoto: attempting to upload new photo.");

        FilePaths filePaths = new FilePaths();
        //case1: new photo
        if(photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "uploadNewPhoto: uploading new photo.");

            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/" + UUID.nameUUIDFromBytes(bytes));



            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String firebaseUrl = uri.toString();
                            photoLocation = location;
                            Log.d(TAG, "onSuccess: Photo uploaded successfully");
                            //add the new photo to 'photos' node and 'user_photos' node
                            switch (type){
                                case "post":
                                    addPhotoToDatabase(caption, firebaseUrl.toString());
                                    break;
                                case "inrange":
                                    addInRangeToDatabase(caption, firebaseUrl, inRange);
                            }
                        }
                    });
                    CommonUtils.dismissProgressDialog();
                    //navigate to the main feed so the user can see the photo

                    Intent intent = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "onFailure: Photo upload failed.");

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });
        }
        //case2: new profile photo (if I get time)

    }

    private void addInRangeToDatabase(String caption, String url, String[] inrangeUsers) {
        Log.d(TAG,"addPhotoToDatabase: adding photo to database");

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String username = SessionManagers.getInstance().getUserDetails().get(Constant.KEY_UNAME);

        // Setup up the inrange model to be written
        InRangePhoto inRangePhoto = new InRangePhoto();
        inRangePhoto.setDate(getTimestamp());
        inRangePhoto.setDone_by_id(user_id);
        inRangePhoto.setImage(url);
        inRangePhoto.setUsername(username);

        // For each user in range, make an entry in the db
        for (String user: inrangeUsers){
            inRangePhoto.setDone_for_id(user);
            mFirestore.collection("friendnearby")
                    .document(UUID.randomUUID().toString())
                    .set(inRangePhoto).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "onComplete: Insertion Successful.");
                }
            });
        }
    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
        return sdf.format(new Date());
    }

    private void addPhotoToDatabase(String caption, String url){
        Log.d(TAG,"addPhotoToDatabase: adding photo to database");

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String username = SessionManagers.getInstance().getUserDetails().get(Constant.KEY_UNAME);


        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate(getTimestamp());
        photo.setImage(url);
        photo.setTags(tags);
        photo.setUid(user_id);
        photo.setUsername(username);
        photo.setPhoto_id(newPhotoKey);
        photo.setLocation(photoLocation);

        //insert into database
        mFirestore.collection("post").document(newPhotoKey).set(photo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: Insertion Successful.");
            }
        });





    }

    public int getImageCount(DataSnapshot dataSnapshot){

        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_photos))
                .getChildren()){
            if(ds.child(mContext.getString(R.string.text_uid)).getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            count++;
        }
        return count;
    }

}
