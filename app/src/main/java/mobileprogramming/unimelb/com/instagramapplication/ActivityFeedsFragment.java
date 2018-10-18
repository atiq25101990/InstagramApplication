package mobileprogramming.unimelb.com.instagramapplication;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityFeedsFragment extends Fragment {

    private View view;
    @BindView(R.id.activityFeedRecyclerView)
    RecyclerView activityFeedRecyclerView;
    private static final String TAG = "ActivityFeedsFragment";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    CollectionReference activityRef = db.collection("activity");

    private String uuid;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<String> following;
    ArrayList<String> followers;
    private ArrayList<Map<String, String>> followerActivity = new ArrayList<>();
    private ArrayList<Map<String, String>> followingActivity = new ArrayList<>();

    public ActivityFeedsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activity_feeds, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        activityFeedRecyclerView.setLayoutManager(linearLayoutManager);
//        mActivityFeedAdapter = new ActivityFeedAdapter(getActivity());
//        activityFeedRecyclerView.setAdapter(mActivityFeedAdapter);


        getActivityFeed();
    }

    private void getActivityFeed() {
        getFollowers();
        getFollowing();


    }

    private void getFollowingActivity(ArrayList<String> following) {
        Query query;

        for (String follower : following) {
            query = activityRef.whereEqualTo("done_by_id", follower).whereEqualTo("done_for_id", currentUser.getUid());

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Map<String, String> instance = new HashMap<>();
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            instance.put("done_by_name", document.getString("done_by_name"));
                            instance.put("done_for_name", document.getString("done_for_name"));
                            instance.put("type", document.getString("type"));
                            instance.put("post_id", document.getString("postid"));
                            instance.put("date", document.getString("date"));
                            followingActivity.add(instance);
                        }
                    }
                }
            });

        }
    }


    private void getFollowerActivity(ArrayList<String> following_list){
        Query query;

        for (String following : following_list) {
            query = activityRef.whereEqualTo("done_for_id", following);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Map<String, String> instance = new HashMap<>();
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            instance.put("done_by_name", document.getString("done_by_name"));
                            instance.put("done_for_name", document.getString("done_for_name"));
                            instance.put("type", document.getString("type"));
                            instance.put("post_id", document.getString("postid"));
                            instance.put("date", document.getString("date"));
                            followerActivity.add(instance);
                        }

                    }
                }
            });

        }
    }


    /**
     * Helper function to calculate the follower count for profile page
     */
    private void getFollowers() {
        final ArrayList<String> userIDs = new ArrayList<>();

        // get the list of followers from collection
        CollectionReference citiesRef = db.collection("follower");

        // query all the users followed by this uuid
        Query query = citiesRef.whereEqualTo("uid", uuid);

        // get a running sum of the number of users
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "getFollowers: calculating the count");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            userIDs.add(document.getString("followerid"));
                        }
                    }

                    followers = userIDs;
                    getFollowerActivity(followers);
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    /**
     * Helper function to calculate the following count for profile page
     */
    private void getFollowing() {
        final ArrayList<String> userIDs = new ArrayList<>();

        CollectionReference citiesRef = db.collection("follower");
        Query query = citiesRef.whereEqualTo("followerid", uuid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "getFollowing: calculating the count");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            userIDs.add(document.getString("uid"));
                        }
                    }
                    following = userIDs;
                    getFollowingActivity(following);
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });


    }

}
