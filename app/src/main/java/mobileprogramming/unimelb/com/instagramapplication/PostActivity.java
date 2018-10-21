package mobileprogramming.unimelb.com.instagramapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnItemClickListener;
import mobileprogramming.unimelb.com.instagramapplication.utils.CommonUtils;
import mobileprogramming.unimelb.com.instagramapplication.utils.SessionManagers;

public class PostActivity extends AppCompatActivity {


    private static final String TAG = "PostActivity";
//    @BindView(R.id.commentsRecycler)
//    RecyclerView commentsRecycler;

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

    @BindView(R.id.photoAnimationView)
    View animView;

    private HashMap<String, String> userDetails;
    private FragmentManager fm;
    private String uuid;
    private String postid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String postLink;
    private String profileImageLink;
    private String profileUserName;
    private String date;
    private String location;
    private ArrayList<Object> comments;
    private ArrayList<Object> likes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        android.support.v7.widget.Toolbar myToolbar = findViewById(R.id.photoToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ButterKnife.bind(this);
        CommonUtils.showLoadingDialog(this);

        userDetails = SessionManagers.getInstance().getUserDetails();

        if (getIntent() != null) {
            postLink = getIntent().getStringExtra("postid");
            profileImageLink = getIntent().getStringExtra("profileURL");
            profileUserName = getIntent().getStringExtra("profileUsername");

        }

        getPostID();
        getLikes();
        getComments();

   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            default:
                    return super.onOptionsItemSelected(item);
        }
    }

    private void getPostID() {
        CollectionReference postRef = db.collection("post");
        Query query = postRef.whereEqualTo("image", postLink);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    postid = document.getId();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    // parsing date
                    Date dateObject = null;
                    try {
                        dateObject = sdf.parse((String) document.get("date"));
                        SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy");

                        date = sdf2.format(dateObject);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    location = document.getString("location");
                    uuid = document.getString("uid");
                }

                setupImages();
            }
        });
    }

    private void setupImages() {
        textUsername.setText(profileUserName);
        textDate.setText(date);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        postImage.setLayoutParams(layoutParams);
        Glide.with(this).load(postLink).into(postImage);
        Glide.with(this).load(profileImageLink).into(background);
        setupListeners();
    }

    private void setupListeners() {
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, FeedCommentsActivity.class);
                intent.putExtra("postid", postid);
                intent.putExtra("username", profileUserName);
                startActivity(intent);
            }
        });
    }


    private void getLikes(){
        CollectionReference likesRef = db.collection("likes");
        Query query = likesRef.whereEqualTo("postid", postid);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                likes = new ArrayList<>();
                for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        likes.add(queryDocumentSnapshot.getString("uid"));

                }
                String text = String.valueOf(likes.size()) + ' ' + getResources().getString(R.string.text_likes);
                textLikes.setText(text);
            }
        });


    }

    private void getComments() {
        CollectionReference likesRef = db.collection("comments");
        Query query = likesRef.whereEqualTo("postid", postid);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                comments = new ArrayList<>();
                for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                    comments.add(queryDocumentSnapshot.getString("uid"));

                }
                CommonUtils.dismissProgressDialog();
                animView.setVisibility(View.GONE);
            }
        });
    }


}
