package mobileprogramming.unimelb.com.instagramapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mobileprogramming.unimelb.com.instagramapplication.adapter.FeedLikesAdapter;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnItemClickListener;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnLoadMoreListener;
import mobileprogramming.unimelb.com.instagramapplication.listener.RecyclerViewLoadMoreScroll;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelLikes;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelUsersFollowing;
import mobileprogramming.unimelb.com.instagramapplication.utils.CommonUtils;
import mobileprogramming.unimelb.com.instagramapplication.utils.Constant;
import mobileprogramming.unimelb.com.instagramapplication.utils.SessionManagers;

public class FeedLikesActivity extends AppCompatActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FeedLikesAdapter adapter;
    private ArrayList<ModelLikes> feeds = new ArrayList<>();
    private ArrayList<ModelUsersFollowing> usersFollowings = new ArrayList<>();
    private RecyclerViewLoadMoreScroll scrollListener;
    private FragmentManager fm;
    private String TAG = "UserFeedsFragment";
    private String uuid;
    private String postid;
    private String username;
    HashMap<String, String> userDetails = new HashMap<>();

    private void setupToolbar() {

        toolbar.setTitle("Likes");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_likes);
        ButterKnife.bind(this);
        setupToolbar();
        fm = getSupportFragmentManager();
        uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (getIntent() != null) {
            postid = getIntent().getStringExtra("postid");
            username = getIntent().getStringExtra("username");
        }
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FeedLikesActivity.this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FeedLikesAdapter(FeedLikesActivity.this, feeds);
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
                    /*
                     * Code here to follow user
                     */

                    final String done_by_id = uuid;
                    final String done_by_name = SessionManagers.getInstance().getUserDetails().get(Constant.KEY_UNAME);
                    final String done_for_name = feeds.get(position).getUsername();
                    final String done_for_id = feeds.get(position).getUuid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("followerid", done_for_id);
                    user.put("uid", done_by_id);
                    user.put("date", Calendar.getInstance().getTime());
                    db.collection("follower")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    adapter.followed(position);
                                    Map<String, Object> activity = new HashMap<>();
                                    activity.put("done_by_id", done_by_id);
                                    // galat
                                    activity.put("done_by_name", done_by_name);

                                    activity.put("done_for_id", done_for_id);
                                    activity.put("done_for_name", done_for_name);
                                    activity.put("date", Calendar.getInstance().getTime());
                                    activity.put("type", "Follow");

                                    db.collection("activity")
                                            .add(activity).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            Log.d(TAG, "onComplete: activity written");
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                } else if (in == 2) {
                    /*
                     * Code here to unfollow user
                     */
                    feeds.get(position).setFollwing(false);
                    adapter.notifyItemChanged(position);
                    CollectionReference citiesRef = db.collection("follower");
                    CollectionReference activityRef = db.collection("activity");

                    final Query actQuery = activityRef.whereEqualTo("done_by_id", uuid).whereEqualTo("done_for_id", feeds.get(position).getUuid());

                    actQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                WriteBatch batch = actQuery.getFirestore().batch();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    batch.delete(document.getReference());
                                }
                                batch.commit();
                            } else {
                                Log.d(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

                    final Query query = citiesRef.whereEqualTo("followerid", uuid).whereEqualTo("uid", feeds.get(position).getUuid());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                WriteBatch batch = query.getFirestore().batch();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    batch.delete(document.getReference());
                                }
                                batch.commit();
                            } else {
                                Log.d(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

                }
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        feeds.clear();
        getFollowingUsers();
    }

    private void LoadMoreData() {
        adapter.addLoadingView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.removeLoadingView();
                scrollListener.setLoaded();
                //mRecyclerView.smoothScrollToPosition(list.size() - 2);
            }
        }, 1000);

    }

    private void getFollowingUsers() {
        CommonUtils.showLoadingDialog(FeedLikesActivity.this);
        CollectionReference citiesRef = db.collection("follower");
        Query query = citiesRef.whereEqualTo("followerid", uuid).limit(100);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                CommonUtils.dismissProgressDialog();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            final ModelUsersFollowing m = new ModelUsersFollowing();

                            m.setUuid(document.getString("uid"));
                            m.setFollowerid(document.getString("followerid"));
                            usersFollowings.add(m);

                        }
                    }
                }
                getUserLikes();
            }
        });


    }

    private void getUserLikes() {
        CollectionReference citiesRef = db.collection("likes");
        Query query = citiesRef.whereEqualTo("postid", postid).limit(50);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            final ModelLikes m = new ModelLikes();
                            m.setUuid(document.getString("uid"));
                            m.setUsername(document.getString("username"));

                            for (int i = 0; i < usersFollowings.size(); i++) {
                                if (usersFollowings.get(i).getUuid().equals(document.getData().get("uid").toString())) {
                                    m.setFollwing(true);
                                    break;
                                }
                            }

                            feeds.add(m);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });


    }
}
