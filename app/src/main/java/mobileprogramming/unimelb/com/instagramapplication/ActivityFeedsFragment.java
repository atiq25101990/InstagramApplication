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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mobileprogramming.unimelb.com.instagramapplication.adapter.HomeFeedAdapter;
import mobileprogramming.unimelb.com.instagramapplication.models.HomeFeedModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityFeedsFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private HomeFeedAdapter adapter;
    private List<HomeFeedModel> feeds = new ArrayList<>();
    private View view;

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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        feeds.add(new HomeFeedModel(""));
        feeds.add(new HomeFeedModel(""));
        feeds.add(new HomeFeedModel(""));
        feeds.add(new HomeFeedModel(""));
        feeds.add(new HomeFeedModel(""));


        adapter = new HomeFeedAdapter(view.getContext(), feeds);
        recyclerView.setAdapter(adapter);
    }
}
