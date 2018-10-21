package mobileprogramming.unimelb.com.instagramapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import mobileprogramming.unimelb.com.instagramapplication.adapter.FeedAdapter;
import mobileprogramming.unimelb.com.instagramapplication.fragment.CustomeDialogFragment;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnItemClickListener;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnItemSelectedListener;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnLoadMoreListener;
import mobileprogramming.unimelb.com.instagramapplication.listener.RecyclerViewLoadMoreScroll;
import mobileprogramming.unimelb.com.instagramapplication.models.ItemSpinner;
import mobileprogramming.unimelb.com.instagramapplication.models.Model;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelUsersFollowing;
import mobileprogramming.unimelb.com.instagramapplication.utils.CommonUtils;
import mobileprogramming.unimelb.com.instagramapplication.utils.Constant;
import mobileprogramming.unimelb.com.instagramapplication.utils.SessionManagers;


public class UserFeedsFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query first;
    @BindView(R.id.fab_sort)
    FloatingActionButton fab_sort;

    @BindView(R.id.animateChangesLayout)
    View animateChangesView;

    private FeedAdapter adapter;
    private ArrayList<Model> feeds = new ArrayList<>();
    private ArrayList<ModelUsersFollowing> usersFollowings = new ArrayList<>();
    private ArrayList<String> friendsInRange = new ArrayList<>();
    private View view;
    private RecyclerViewLoadMoreScroll scrollListener;
    private FragmentManager fm;

    public String getTAG() {
        return TAG;
    }

    private String TAG = "UserFeedsFragment";
    private String uuid;

    HashMap<String, String> userDetails = new HashMap<>();

    public UserFeedsFragment() {
    }

    public static UserFeedsFragment newInstance() {
        return new UserFeedsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_userfeeds, container, false);
        fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        CommonUtils.showLoadingDialog(getContext());
        uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        friendsInRange.clear();
        getMyFriendsInRange();
        feeds.clear();
        getFeedsInRange(0);


        userDetails = SessionManagers.getInstance().getUserDetails();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FeedAdapter((AppCompatActivity) getActivity(), feeds);
        recyclerView.setAdapter(adapter);
        scrollListener = new RecyclerViewLoadMoreScroll(linearLayoutManager);
        scrollListener.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                LoadMoreData();
            }
        });
        recyclerView.addOnScrollListener(scrollListener);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final int position, int in) {
                if (in == 1) {

                    CollectionReference citiesRef = db.collection("likes");
                    final Query query = citiesRef.whereEqualTo("uid", uuid).whereEqualTo("postid", feeds.get(position).getPostid());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    Map<String, Object> likeObject = new HashMap<>();

                                    final String done_by_id = uuid;
                                    final String done_by_name = SessionManagers.getInstance().getUserDetails().get(Constant.KEY_UNAME);
                                    final String done_for_name = feeds.get(position).getUsername();
                                    final String done_for_id = feeds.get(position).getUuid();

                                    likeObject.put("postid", feeds.get(position).getPostid());
                                    likeObject.put("uid", uuid);
                                    likeObject.put("username", userDetails.get(Constant.KEY_UNAME));
                                    likeObject.put("date", Calendar.getInstance().getTime());
                                    Map<String, Object> activity = new HashMap<>();
                                    activity.put("done_by_id", done_by_id);
                                    activity.put("done_by_name", done_by_name);

                                    activity.put("done_for_id", feeds.get(position).getUuid());
                                    activity.put("done_for_name", done_for_name);
                                    activity.put("date", Calendar.getInstance().getTime());
                                    activity.put("type", "Like");
                                    db.collection("likes")
                                            .add(likeObject)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(getContext(), "Liked  ", Toast.LENGTH_SHORT).show();
                                                    adapter.likeClicked(position);
                                                }
                                            });

                                    db.collection("activity")
                                            .add(activity)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d(TAG, "onSuccess: activity written");
                                                }
                                            });
                                } else {
                                    String docID = task.getResult().getDocuments().get(0).getId();
                                    db.collection("likes").document(docID)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    adapter.unlikeClicked(position);
                                                }
                                            });
                                }
                            }
                        }
                    });


                }


            }

            @Override
            public void onItemLongClick(int position) {

            }
        });

        fab_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ArrayList<ItemSpinner> itemOption = new ArrayList<>();
                itemOption.add(new ItemSpinner(0, "Date/Time"));
                itemOption.add(new ItemSpinner(1, "Location"));

                CustomeDialogFragment customeDialogFragment = new CustomeDialogFragment(getContext(), R.layout.custome_dialog_fragment_withtitle, true, "Sort by", itemOption);
                customeDialogFragment.setCancelable(false);
                customeDialogFragment.show(fm, "");

                customeDialogFragment.setItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(ItemSpinner itemSpinner) {
                        feeds.clear();
                        getFeedsInRange(0);
                        getFeeds(itemSpinner.getId());

                    }
                });
            }
        });
        feeds.clear();
        getFollowingUsers();

    }

    private void getMyFriendsInRange()
    {
        CollectionReference inRangeFriends = db.collection("friendnearby");
        Query query = inRangeFriends.whereEqualTo("done_for_id", uuid).limit(100);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                          //document.getData().get("uid").toString()
                            //Timestamp ts = document.getTimestamp("timestamp");


                            //Checking if within 15 minutes!
                            long prevEventTime = System.currentTimeMillis();


                            friendsInRange.add(document.getData().get("done_by_id").toString());
                        }
                    }
                }

            }
        });

    }

    private void LoadMoreData() {
        adapter.addLoadingView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.removeLoadingView();
                scrollListener.setLoaded();

            }
        }, 1000);

    }

    private void getFollowingUsers() {
        CollectionReference citiesRef = db.collection("follower");
        Query query = citiesRef.whereEqualTo("followerid", uuid).limit(100);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    usersFollowings.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                            final ModelUsersFollowing m = new ModelUsersFollowing();
                            m.setUuid(document.getData().get("uid").toString());
                            m.setFollowerid(document.getData().get("followerid").toString());
                            usersFollowings.add(m);
                    }
                    Log.d(TAG, "onComplete: Following users are: " + usersFollowings.size());
                    //getFeedsInRange(0);
                    getFeeds(0);



                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });


    }

    private void getFeeds(int id) {
        //id ==0 for date/time
        //id ==1 for location
        //default id 0
        Log.d("selected", "=>" + id);
        Log.d("Range Size: ","Size: "+friendsInRange.size());
        CollectionReference citiesRef = db.collection("post");
        Query query = citiesRef;
//        if (id == 0) {
//            query = citiesRef.orderBy("date", Query.Direction.DESCENDING).limit(100);
//        } else {
//            query = citiesRef.orderBy("location", Query.Direction.ASCENDING).limit(100);
//        }
        //please make change as per requirement in future
        //Query query = citiesRef.orderBy("date", Query.Direction.DESCENDING).orderBy("location",Query.Direction.ASCENDING).limit(100);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Fetched Posts");
                            final Model m = new Model();

                            m.setPostid(document.getId());
                            m.setImage(document.getData().get("image").toString());
                            m.setUuid(document.getData().get("uid").toString());
                            m.setDate(document.getData().get("date").toString());
                            m.setCaption(document.getString("caption"));
                            Log.d(TAG, document.getData().get("date").toString());
                            try {
                                m.setUsername(String.valueOf(document.getData().get("username")));
                            } catch (Exception e){
                                Log.d(TAG, "onComplete: username null");
                            }


                            if(uuid.equals(document.getData().get("uid").toString())){
                                m.setRange(false);
                                feeds.add(m);
                            } else {
                                for (int i = 0; i < usersFollowings.size(); i++) {
                                    if (usersFollowings.get(i).getUuid().equals(document.getData().get("uid").toString())) {
                                        m.setRange(false);
                                        feeds.add(m);
                                        break;
                                    }
                                }
                            }


                            adapter.notifyDataSetChanged();
                            animateChangesView.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }

                CommonUtils.dismissProgressDialog();
            }
        });
    }

    private void getFeedsInRange(int id) {
        //id ==0 for date/time
        //id ==1 for location
        //default id 0
        Log.d("selected", "=>" + id);
        Log.d("Range Size: ","Size: "+friendsInRange.size());
        CollectionReference inRangeFriends = db.collection("friendnearby");
        Query query = inRangeFriends.whereEqualTo("done_for_id", uuid).limit(100);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            final Model m = new Model();
                            m.setPostid(document.getId());
                            m.setImage(document.getData().get("image").toString());
                            m.setUuid(document.getData().get("done_by_id").toString());
                            m.setUsername(document.getData().get("username").toString());
                            m.setDate(document.getData().get("date").toString());
                            Log.d(TAG, document.getData().get("date").toString());
                            Log.d("Range: ","Populating pictues in range: "+document.getData().get("done_by_id").toString());
                            m.setRange(true);
                            feeds.add(m);

                            adapter.notifyDataSetChanged();
                            CommonUtils.dismissProgressDialog();
                            animateChangesView.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }


}
