package mobileprogramming.unimelb.com.instagramapplication;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import mobileprogramming.unimelb.com.instagramapplication.adapter.ProfileImageAdapter;
import mobileprogramming.unimelb.com.instagramapplication.utils.CommonUtils;
import mobileprogramming.unimelb.com.instagramapplication.utils.SessionManagers;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private HashMap<String, String> userDetails;
    private String uuid;

    @BindView(R.id.profileCircularPicture)
    CircleImageView profileCircularPicture;

    @BindView(R.id.textViewPosts)
    TextView textViewPosts;
    @BindView(R.id.textViewFollowers)
    TextView textViewFollowers;
    @BindView(R.id.textViewFollowing)
    TextView textViewFollowing;
    @BindView(R.id.profileDisplayName)
    TextView profileDisplayName;
    @BindView(R.id.profilePictureGrid)
    GridView profilePictureGrid;

    @BindView(R.id.profile_user_name)
    TextView profileUserName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;
    private int mFollowersCount;
    private int mFollowingCount;
    private int mPostCount;
    private ArrayList<String> urlList = new ArrayList<>();


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflating fragment and setting fragment manager
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Binding views with Butterknife
        ButterKnife.bind(this, view);

        // Getting user details from Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uuid = currentUser.getUid();
        userDetails = SessionManagers.getInstance().getUserDetails();

        CommonUtils.showLoadingDialog(getContext());
        getPostedImages();
        getFollowingCount();
        getFollowerCount();
        setProfilePicture();
        getPostCount();

    }

    private void setProfilePicture() {
        DocumentReference userDoc = db.collection("Users").document(uuid);

        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        profileUserName.setText(String.valueOf(document.getString("username")));
                        profileDisplayName.setText(String.valueOf(document.getString("name")));
                        Glide.with(getContext()).load(document.getString("image")).into(profileCircularPicture);
                    } else {
                        Log.d(TAG, "onComplete: Failed to get document");
                    }
                }
            }
        });

    }


    private void getPostedImages() {

        CollectionReference citiesRef = db.collection("post");

        Query query = citiesRef.whereEqualTo("uid", uuid);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    for (QueryDocumentSnapshot documentReference : querySnapshot) {
                        urlList.add(documentReference.getString("image"));
                    }
                    setPostedImages();
                }
            }
        });

    }

    private void setPostedImages() {
        ProfileImageAdapter profileImageAdapter = new ProfileImageAdapter(getContext());
        profilePictureGrid.setAdapter(profileImageAdapter);
        profileImageAdapter.setUrlList(urlList);
        profileImageAdapter.notifyDataSetChanged();
    }

    /**
     * Helper function to calculate the follower count for profile page
     */
    private void getFollowerCount() {
        mFollowingCount = 0;

        // get the list of followers from collection
        CollectionReference citiesRef = db.collection("follower");

        // query all the users followed by this uuid
        Query query = citiesRef.whereEqualTo("uuid", uuid);

        // get a running sum of the number of users
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "getFollowerCount: calculating the count");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            mFollowersCount++;
                        }
                    }
                    textViewFollowers.setText(String.valueOf(mFollowersCount));
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    /**
     * Helper function to calculate the post count for profile page
     */
    private void getPostCount() {
        mPostCount = 0;

        CollectionReference citiesRef = db.collection("post");
        Query query = citiesRef.whereEqualTo("uid", uuid);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    CommonUtils.dismissProgressDialog();
                    Log.d(TAG, "getPostCount: calculating the count");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            mPostCount++;
                        }
                    }
                    textViewPosts.setText(String.valueOf(mPostCount));
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    /**
     * Helper function to calculate the following count for profile page
     */
    private void getFollowingCount() {
        mFollowersCount = 0;

        CollectionReference citiesRef = db.collection("follower");
        Query query = citiesRef.whereEqualTo("followerid", uuid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "getFollowingCount: calculating the count");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            mFollowingCount++;
                        }
                    }
                    textViewFollowing.setText(String.valueOf(mFollowingCount));
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });

    }


}
