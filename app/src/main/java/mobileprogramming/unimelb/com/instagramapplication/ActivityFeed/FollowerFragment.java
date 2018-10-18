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

    private ArrayList<Map<String, String>> followerActivity = new ArrayList<>();
    ActivityFollowerAdapter mActivityFollowerAdapter;

    public FollowerFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Creating view");
        getFollowerActivity();
        return inflater.inflate(
                R.layout.fragment_follower, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        followerActivityRecycler.setLayoutManager(linearLayoutManager);
        mActivityFollowerAdapter = new ActivityFollowerAdapter(getActivity());
        followerActivityRecycler.setAdapter(mActivityFollowerAdapter);

    }


    private void getFollowerActivity() {
        Query query = activityRef.whereEqualTo("done_for_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, String> instance = new HashMap<>();
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        instance.put("done_by_name", document.getString("done_by_name"));
                        instance.put("done_by_id", document.getString("done_by_id"));
                        instance.put("done_for_name", document.getString("done_for_name"));
                        instance.put("done_for_id", document.getString("done_for_id"));
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
