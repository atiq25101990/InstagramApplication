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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import mobileprogramming.unimelb.com.instagramapplication.adapter.FeedCommentsAdapter;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnLoadMoreListener;
import mobileprogramming.unimelb.com.instagramapplication.listener.RecyclerViewLoadMoreScroll;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelLikes;
import mobileprogramming.unimelb.com.instagramapplication.utils.Constant;
import mobileprogramming.unimelb.com.instagramapplication.utils.SessionManagers;

public class FeedCommentsActivity extends AppCompatActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_send)
    ImageButton btn_send;
    @BindView(R.id.edt_comments)
    EditText edt_comments;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FeedCommentsAdapter adapter;
    private ArrayList<ModelLikes> feeds = new ArrayList<>();
    private RecyclerViewLoadMoreScroll scrollListener;
    private FragmentManager fm;
    private String TAG = "UserFeedsFragment";
    private String uuid;
    private String postid;
    HashMap<String, String> userDetails = new HashMap<>();

    private void setupToolbar() {
        toolbar.setTitle("Comments");
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
        setContentView(R.layout.activity_feed_comments);
        ButterKnife.bind(this);
        setupToolbar();
        userDetails=SessionManagers.getInstance().getUserDetails();
        fm = getSupportFragmentManager();
        uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (getIntent() != null) {
            postid = getIntent().getStringExtra("postid");

        }
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FeedCommentsActivity.this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FeedCommentsAdapter(FeedCommentsActivity.this, feeds);
        recyclerView.setAdapter(adapter);
        scrollListener = new RecyclerViewLoadMoreScroll(linearLayoutManager);
        scrollListener.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
//LoadMoreData();
            }
        });
        recyclerView.addOnScrollListener(scrollListener);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_comments.getText().length() > 0) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("postid", postid);
                    user.put("uid", uuid);
                    user.put("text", edt_comments.getText().toString());
                    user.put("commenttime", FieldValue.serverTimestamp());
                    user.put("username", userDetails.get(Constant.KEY_UNAME));
                    db.collection("comments").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            final ModelLikes m = new ModelLikes();
                            m.setUuid(uuid);
                            m.setUsername(userDetails.get(Constant.KEY_UNAME));
                            m.setText(edt_comments.getText().toString());
                            feeds.add(m);
                            adapter.notifyDataSetChanged();
                            edt_comments.setText("");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                } else {
                    Toast.makeText(FeedCommentsActivity.this, "Empty comment not allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getUserComments();
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

    private void getUserComments() {
        feeds.clear();

        CollectionReference citiesRef = db.collection("comments");
        Query query = citiesRef.whereEqualTo("postid", postid).orderBy("commenttime", Query.Direction.ASCENDING).limit(100);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    try {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            final ModelLikes m = new ModelLikes();
                            m.setUuid(document.getData().get("uid").toString());
                            m.setUsername(document.getData().get("username").toString());
                            m.setText(document.getData().get("text").toString());
                            feeds.add(m);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                    }
                } else {
                    Log.d(TAG, "Error getting documents.", task.getException());
                }
            }
        });


    }
}
