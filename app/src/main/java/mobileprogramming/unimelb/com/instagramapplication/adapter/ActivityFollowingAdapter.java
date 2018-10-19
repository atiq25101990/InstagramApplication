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

import java.util.LinkedHashSet;

import de.hdodenhof.circleimageview.CircleImageView;
import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelActivity;

public class ActivityFollowingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final FragmentActivity mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ActivityFollowingAdapter(FragmentActivity context) {
        mContext = context;
    }

    public void setFollowingActivity(LinkedHashSet<ModelActivity> followingActivity) {
        this.followingActivity = followingActivity;
    }

    private LinkedHashSet<ModelActivity> followingActivity = new LinkedHashSet<>();
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FeedViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_feed_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelActivity item = (ModelActivity) followingActivity.toArray()[position];
        getUserPhoto(item.getDoneByID(), ((ActivityFollowingAdapter.FeedViewHolder) holder).feedUserImage);


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

        ((ActivityFollowingAdapter.FeedViewHolder) holder).feedText.setText(feedText);

        if(item.getPostid()!=null){
            getPostURL(item.getPostid(), ((ActivityFollowingAdapter.FeedViewHolder) holder).feedPostImage);
        }
    }


    private void setPostImage(String image, ImageView feedPostImage) {
        Glide.with(mContext.getApplicationContext()).load(image).into(feedPostImage);
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
        Glide.with(mContext.getApplicationContext()).load(image).into(feedUserImage);

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

    @Override
    public int getItemCount() {
        return followingActivity.size();
    }


    class FeedViewHolder extends RecyclerView.ViewHolder{

        CircleImageView feedUserImage;
        TextView feedText;
        ImageView feedPostImage;

        FeedViewHolder(View view) {
            super(view);

            feedUserImage = view.findViewById(R.id.feedUserImage);
            feedText = view.findViewById(R.id.feedText);
            feedPostImage = view.findViewById(R.id.feedPostImage);
        }
    }
}
