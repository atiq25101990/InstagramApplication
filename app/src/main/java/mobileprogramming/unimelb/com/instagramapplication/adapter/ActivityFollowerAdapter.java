package mobileprogramming.unimelb.com.instagramapplication.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import de.hdodenhof.circleimageview.CircleImageView;
import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelActivity;

public class ActivityFollowerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final FragmentActivity mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public void setFollowerActivity(LinkedHashSet<ModelActivity> followerActivity) {
        this.followerActivity = followerActivity;
    }

    private LinkedHashSet<ModelActivity> followerActivity = new LinkedHashSet<>();

    public ActivityFollowerAdapter(FragmentActivity activity) {
        mContext = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FeedViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_feed_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelActivity item = (ModelActivity) followerActivity.toArray()[position];
        getUserPhoto(item.getDoneByID(), ((FeedViewHolder) holder).feedUserImage);


        String feedText;
        switch (item.getType()){
            case "Like":
                feedText = item.getDoneByName() + " liked " + item.getDoneForName() + "'s post.";
                break;
            case "Comment":
                feedText = item.getDoneByName() + " commented on " + item.getDoneForName() + "'s post.";
                break;
            case "Follow":
                feedText = item.getDoneByName() + " started following " + item.getDoneForName() + ".";
                break;
            default:
                feedText = item.getDoneByName() + " interacted with " + item.getDoneForName() + ".";
                break;
        }

        ((FeedViewHolder) holder).feedText.setText(feedText);

        if(item.getPostid()!=null){
            getPostURL(item.getPostid(), ((FeedViewHolder) holder).feedPostImage);
        }
    }

    private void getPostURL(String postid, final ImageView feedPostImage) {
        DocumentReference postRef = db.collection("post").document(postid);

        postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    setPostImage(task.getResult().getString("image"), feedPostImage);
                }
            }
        });
    }

    private void setPostImage(String image, ImageView feedPostImage) {
        Glide.with(mContext).load(image).into(feedPostImage);
    }

    private void getUserPhoto(String done_by_id, final CircleImageView feedUserImage) {
        DocumentReference userRef = db.collection("Users").document(done_by_id);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    setUserImage(task.getResult().getString("image"), feedUserImage);
                }
            }
        });
    }

    private void setUserImage(String image, CircleImageView feedUserImage) {
        Glide.with(mContext).load(image).into(feedUserImage);

    }

    @Override
    public int getItemCount() {
        return followerActivity.size();
    }

    class FeedViewHolder extends RecyclerView.ViewHolder {
        CircleImageView feedUserImage;
        TextView feedText;
        ImageView feedPostImage;

        public FeedViewHolder(View view) {
            super(view);

            feedUserImage = view.findViewById(R.id.feedUserImage);
            feedText = view.findViewById(R.id.feedText);
            feedPostImage = view.findViewById(R.id.feedPostImage);
        }
    }
}
