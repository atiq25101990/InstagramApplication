package mobileprogramming.unimelb.com.instagramapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mobileprogramming.unimelb.com.instagramapplication.adapter.FeedAdapter;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnItemClickListener;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnLoadMoreListener;
import mobileprogramming.unimelb.com.instagramapplication.listener.RecyclerViewLoadMoreScroll;
import mobileprogramming.unimelb.com.instagramapplication.models.Model;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelUsersFollowing;
import mobileprogramming.unimelb.com.instagramapplication.utils.CommonUtils;


public class UserFeedsFragment extends Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query first;

    private FeedAdapter adapter;
    private ArrayList<Model> feeds = new ArrayList<>();
    private ArrayList<ModelUsersFollowing> usersFollowings = new ArrayList<>();
    private View view;
    private RecyclerViewLoadMoreScroll scrollListener;
    private FragmentManager fm;
    private String TAG = "UserFeedsFragment";
    private String uuid;

    public UserFeedsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_userfeeds, container, false);
        fm = getActivity().getSupportFragmentManager();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();


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
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("postid", feeds.get(position).getPostid());
                                    user.put("uid", uuid);
                                    user.put("username", MainActivity.username);
                                    db.collection("likes")
                                            .add(user)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(getContext(), "Liked  ", Toast.LENGTH_SHORT).show();
                                                    adapter.likeClicked(position);
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

            }
        }, 1000);

    }

    private void getFollowingUsers() {
        CommonUtils.showLoadingDialog(getContext());
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

                            m.setUuid(document.getData().get("uid").toString());
                            m.setFollowerid(document.getData().get("followerid").toString());
                            usersFollowings.add(m);

                        }
                    }
                    getFeeds();

                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });


    }

    private void getFeeds() {

        CommonUtils.showLoadingDialog(getContext());
        CollectionReference citiesRef = db.collection("post");
        Query query = citiesRef.orderBy("date", Query.Direction.ASCENDING).orderBy("location", Query.Direction.ASCENDING).limit(100);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        CommonUtils.dismissProgressDialog();
                        if (task.isSuccessful()) {
                            final Model m = new Model();
                            m.setPostid(document.getId());
                            m.setImage(document.getData().get("image").toString());
                            m.setUuid(document.getData().get("uid").toString());
                            m.setUsername(document.getData().get("username").toString());
                            m.setDate(document.getData().get("date").toString());
                            Log.d(TAG, document.getData().get("date").toString());
                            CollectionReference citiesRef = db.collection("likes");
                            Query query = citiesRef.whereEqualTo("postid", document.getId());
                            CommonUtils.showLoadingDialog(getContext());
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    CommonUtils.dismissProgressDialog();

                                    if (task.isSuccessful()) {
                                        m.setLikes(task.getResult().size());
                                    }
                                    for (int i = 0; i < usersFollowings.size(); i++) {
                                        if (usersFollowings.get(i).getUuid().equals(document.getData().get("uid").toString())) {
                                            feeds.add(m);
                                            break;
                                        }
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            });

                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });


//        // Construct query for first 5 post, ordered by postid
//        first = db.collection("post")
//                .orderBy("postid")
//                .limit(5);
//        first.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot documentSnapshots) {
//                // Get the last visible document
//              //  DocumentSnapshot lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
//
//                for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
//                    final Model m = new Model();
//                    m.setPostid(document.getId());
//                    m.setImage(document.getData().get("image").toString());
//                    m.setUuid(document.getData().get("uid").toString());
//                    m.setUsername(document.getData().get("username").toString());
//
//                    CollectionReference citiesRef = db.collection("likes");
//                    Query query = citiesRef.whereEqualTo("postid", document.getId());
//                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            pbar.setVisibility(View.GONE);
//
//                            if (task.isSuccessful()) {
//                                m.setLikes(task.getResult().size());
//                            }
//                            feeds.add(m);
//                            adapter.notifyDataSetChanged();
//                        }
//                    });
//                }
//
//                // Construct a new query starting at this document,
//                // get the next 25 cities.
//                first = db.collection("post")
//                        .orderBy("postid")
//                        .limit(5);
//
//
//            }
//        });


    }
}
