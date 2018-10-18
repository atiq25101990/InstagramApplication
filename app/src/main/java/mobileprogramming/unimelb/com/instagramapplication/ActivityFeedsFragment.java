package mobileprogramming.unimelb.com.instagramapplication;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import mobileprogramming.unimelb.com.instagramapplication.adapter.ActivityFeedAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityFeedsFragment extends Fragment {

    private View view;
    @BindView(R.id.activityFeedRecyclerView)
    RecyclerView activityFeedRecyclerView;
    private static final String TAG = "ActivityFeedsFragment";

    ActivityFeedAdapter mActivityFeedAdapter;
    private String uuid;

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
        mActivityFeedAdapter = new ActivityFeedAdapter(getActivity());
        activityFeedRecyclerView.setAdapter(mActivityFeedAdapter);


    }
}
