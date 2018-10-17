package mobileprogramming.unimelb.com.instagramapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
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

    public ActivityFeedsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activity_feeds, container, false);
        return view;
    }


}
