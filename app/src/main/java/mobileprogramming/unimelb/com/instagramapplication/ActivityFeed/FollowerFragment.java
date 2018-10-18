package mobileprogramming.unimelb.com.instagramapplication.ActivityFeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.adapter.ActivityFollowerAdapter;

public class FollowerFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "FollowerFragment";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @BindView(R.id.followerActivityRecycler)
    RecyclerView followerActivityRecycler;

    CollectionReference activityRef = db.collection("activity");


    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    ArrayList<String> followers;
    private ArrayList<Map<String, String>> followerActivity = new ArrayList<>();
    ActivityFollowerAdapter mActivityFollowerAdapter;
    private String uuid;

    public FollowerFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creating view");
        getFollowers();
        return inflater.inflate(
                R.layout.fragment_follower, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        followerActivityRecycler.setLayoutManager(linearLayoutManager);
        mActivityFollowerAdapter = new ActivityFollowerAdapter(getActivity());
        followerActivityRecycler.setAdapter(mActivityFollowerAdapter);

    }


    /**
     * Helper function to calculate the follower count for profile page
     */
    private void getFollowers() {
        final ArrayList<String> userIDs = new ArrayList<>();

        // get the list of followers from collection
        CollectionReference citiesRef = db.collection("follower");

        // query all the users followed by this uuid
        Query query = citiesRef.whereEqualTo("uid", currentUser.getUid());

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


    private void getFollowerActivity(ArrayList<String> following_list) {
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
                            instance.put("done_by_id", document.getString("done_by_id"));
                            instance.put("done_for_name", document.getString("done_for_name"));
                            instance.put("don_for_id", document.getString("done_for_id"));
                            instance.put("type", document.getString("type"));
                            instance.put("post_id", document.getString("postid"));
                            instance.put("date", String.valueOf(document.get("date")));
                            followerActivity.add(instance);
                        }
                        mActivityFollowerAdapter.setFollowerActivity(followerActivity);
                        mActivityFollowerAdapter.notifyDataSetChanged();
                    }
                }
            });

        }
    }

}
