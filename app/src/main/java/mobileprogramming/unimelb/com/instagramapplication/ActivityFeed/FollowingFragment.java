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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.adapter.ActivityFollowingAdapter;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelActivity;

public class FollowingFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "FollowingFragment";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @BindView(R.id.followingActivityRecycler)
    RecyclerView followingActivityRecycler;

    CollectionReference activityRef = db.collection("activity");

    private LinkedHashSet<ModelActivity> followingActivity = new LinkedHashSet<>();
    ActivityFollowingAdapter mActivityFollowingAdapter;

    public FollowingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creating view");
        getFollowing();
        return (ViewGroup) inflater.inflate(
                R.layout.fragment_following, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        followingActivityRecycler.setLayoutManager(linearLayoutManager);
        mActivityFollowingAdapter = new ActivityFollowingAdapter(getActivity());
        followingActivityRecycler.setAdapter(mActivityFollowingAdapter);
    }

    private void getFollowingActivity(ArrayList<String> userIDs) {
        Query query;

        for(String user: userIDs){
            query = activityRef.whereEqualTo("done_by_id", user);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ModelActivity modelActivity = new ModelActivity();
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            modelActivity.setDoneByName(document.getString("done_by_name"));
                            modelActivity.setDoneByID(document.getString("done_by_id"));
                            modelActivity.setDoneForName(document.getString("done_for_name"));
                            modelActivity.setDoneForID(document.getString("done_for_id"));
                            modelActivity.setType(document.getString("type"));
                            modelActivity.setPostid(document.getString("postid"));
                            modelActivity.setDate(String.valueOf(document.get("date")));
                            followingActivity.add(modelActivity);
                        }
                        mActivityFollowingAdapter.setFollowingActivity(followingActivity);
                        mActivityFollowingAdapter.notifyDataSetChanged();
                    }
                }
            });

        }
    }

    /**
     * Helper function to calculate the following count for profile page
     */
    private void getFollowing() {
        final ArrayList<String> userIDs = new ArrayList<>();

        CollectionReference citiesRef = db.collection("follower");
        Query query = citiesRef.whereEqualTo("followerid", FirebaseAuth.getInstance().getCurrentUser().getUid());
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

                    getFollowingActivity(userIDs);
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });


    }
}
