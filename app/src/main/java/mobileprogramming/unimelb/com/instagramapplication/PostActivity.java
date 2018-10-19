package mobileprogramming.unimelb.com.instagramapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import de.hdodenhof.circleimageview.CircleImageView;
import mobileprogramming.unimelb.com.instagramapplication.adapter.FeedCommentsAdapter;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnLoadMoreListener;
import mobileprogramming.unimelb.com.instagramapplication.listener.RecyclerViewLoadMoreScroll;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelLikes;
import mobileprogramming.unimelb.com.instagramapplication.utils.Constant;
import mobileprogramming.unimelb.com.instagramapplication.utils.SessionManagers;

public class PostActivity extends AppCompatActivity {


    private static final String TAG = "PostActivity";
    @BindView(R.id.commentsRecycler)
    RecyclerView commentsRecycler;

    @BindView(R.id.background)
    CircleImageView background;

    @BindView(R.id.txt_username)
    TextView textUsername;

    @BindView(R.id.txt_date)
    TextView textDate;

    @BindView(R.id.post_image)
    ImageView postImage;

    @BindView(R.id.btn_like)
    ImageView btnLike;

    @BindView(R.id.button_comment)
    ImageView buttonComment;

    @BindView(R.id.txt_likes)
    TextView textLikes;

    private HashMap<String, String> userDetails;
    private FragmentManager fm;
    private String uuid;
    private String postid;
    private FeedCommentsAdapter adapter;
//    @BindView(R.id.btn_send)
//    ImageButton btn_send;
//    @BindView(R.id.edt_comments)
//    EditText edt_comments;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<ModelLikes> feeds;
    private RecyclerViewLoadMoreScroll scrollListener;
    private String postLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ButterKnife.bind(this);

        userDetails = SessionManagers.getInstance().getUserDetails();
        fm = getSupportFragmentManager();
        uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (getIntent() != null) {
            postLink = getIntent().getStringExtra("postid");

        }

        getPostID();

        commentsRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostActivity.this, OrientationHelper.VERTICAL, false);
        commentsRecycler.setLayoutManager(linearLayoutManager);

        adapter = new FeedCommentsAdapter(this, feeds);
        commentsRecycler.setAdapter(adapter);
        scrollListener = new RecyclerViewLoadMoreScroll(linearLayoutManager);
        scrollListener.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
//LoadMoreData();
            }
        });
        commentsRecycler.addOnScrollListener(scrollListener);


    }

    private void getPostID() {
        CollectionReference postRef = db.collection("post");
        Query query = postRef.whereEqualTo("image", postLink);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    postid = document.getId();

                }

                setupListener();
            }
        });
    }

    private void setupListener() {
//        btn_send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (edt_comments.getText().length() > 0) {
//                    Map<String, Object> comment = new HashMap<>();
//                    comment.put("postid", postid);
//                    comment.put("uid", uuid);
//                    comment.put("text", edt_comments.getText().toString());
//                    comment.put("commenttime", FieldValue.serverTimestamp());
//                    comment.put("username", userDetails.get(Constant.KEY_UNAME));
//                    db.collection("comments").add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            final ModelLikes m = new ModelLikes();
//                            m.setUuid(uuid);
//                            m.setUsername(userDetails.get(Constant.KEY_UNAME));
//                            m.setText(edt_comments.getText().toString());
//                            feeds.add(m);
//                            adapter.notifyDataSetChanged();
//                            edt_comments.setText("");
//                        }
//                    })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                }
//                            });
//                } else {
//                    Toast.makeText(PostActivity.this, "Empty comment not allowed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        getUserComments();
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
