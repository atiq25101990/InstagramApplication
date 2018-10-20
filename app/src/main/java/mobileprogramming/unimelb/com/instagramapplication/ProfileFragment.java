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
import mobileprogramming.unimelb.com.instagramapplication.utils.Constant;
import mobileprogramming.unimelb.com.instagramapplication.utils.SessionManagers;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private String uuid;

    public String getTAG() {
        return TAG;
    }

    @BindView(R.id.profileCircularPicture)
    CircleImageView profileCircularPicture;
    @BindView(R.id.textEditProfile)
    TextView textViewEditProfile;
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
//    private FirebaseUser currentUser;
    private String currentUserID;
    private int mFollowersCount;
    private int mFollowingCount;
    private int mPostCount;
    private ArrayList<String> urlList = new ArrayList<>();
    private String imageURL;
    private HashMap<String, String> profileUser;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
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

        assert getArguments() != null;
        uuid = getArguments().getString("uid");
        // Getting user details from Firebase
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setProfilePicture();
        if (!uuid.equals(currentUserID)){
            if(getArguments().getBoolean("followed")){
                textViewEditProfile.setText("Unfollow");
            } else {
                textViewEditProfile.setText("Follow");
            }
        }
        Log.d(TAG, "onViewCreated: The username is " + uuid);

        getFollowingCount();
        getFollowerCount();
        getPostCount();

    }

    private void setProfilePicture() {
        DocumentReference userDoc = db.collection("Users").document(uuid);

        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    profileUser = new HashMap<>();
                    if (document.exists()) {
                        profileUser.put("username", document.getString("username"));
                        profileUser.put("imageURL", document.getString("image"));

                        profileUserName.setText(profileUser.get("username"));
                        profileDisplayName.setText(profileUser.get("name"));
                        Glide.with(getContext()).load(document.getString("image")).into(profileCircularPicture);

                        getPostedImages();
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
                    urlList.clear();
                    for (QueryDocumentSnapshot documentReference : querySnapshot) {
                        urlList.add(documentReference.getString("image"));
                    }
                    setPostedImages();
                }
            }
        });

    }

    private void setPostedImages() {
        ProfileImageAdapter profileImageAdapter = new ProfileImageAdapter(getContext(), profileUser);
        profileImageAdapter.setUrlList(urlList);
        profileImageAdapter.notifyDataSetChanged();
        profilePictureGrid.setAdapter(profileImageAdapter);
    }

    /**
     * Helper function to calculate the follower count for profile page
     */
    private void getFollowerCount() {
        mFollowingCount = 0;

        // get the list of followers from collection
        CollectionReference citiesRef = db.collection("follower");

        // query all the users followed by this uuid
        Query query = citiesRef.whereEqualTo("uid", uuid);

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
