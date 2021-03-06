package mobileprogramming.unimelb.com.instagramapplication.adapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import mobileprogramming.unimelb.com.instagramapplication.FeedCommentsActivity;
import mobileprogramming.unimelb.com.instagramapplication.FeedLikesActivity;
import mobileprogramming.unimelb.com.instagramapplication.MainActivity;
import mobileprogramming.unimelb.com.instagramapplication.ProfileFragment;
import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnItemClickListener;
import mobileprogramming.unimelb.com.instagramapplication.models.Model;
import mobileprogramming.unimelb.com.instagramapplication.utils.Constant;
import mobileprogramming.unimelb.com.instagramapplication.viewholder.LoadingHolder;


public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    AppCompatActivity mContext;
    int total_types;
    MediaPlayer mPlayer;
    private OnItemClickListener onItemClickListener;
    private ArrayList<Model> dataSet;
    private boolean fabStateVolume = false;
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf;

    public FeedAdapter(AppCompatActivity context, ArrayList<Model> data) {
        this.dataSet = data;
        this.mContext = context;
        total_types = dataSet.size();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {

            case Model.IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type_1, parent, false);
                return new ImageTypeViewHolder(view);

            case Constant.VIEW_TYPE_LOADING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_loading, parent, false);
                return new LoadingHolder(view);

        }
        return null;


    }

    public void addLoadingView() {
        //add loading item
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                dataSet.add(null);
                notifyItemInserted(dataSet.size() - 1);
            }
        });
    }

    public void removeLoadingView() {
        //Remove loading item
        dataSet.remove(dataSet.size() - 1);
        notifyItemRemoved(dataSet.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (dataSet.get(position) == null)
            return Constant.VIEW_TYPE_LOADING;
        else {
            switch (dataSet.get(position).getType()) {
                case 0:
                    return Model.TYPE_HEADER;
                case 1:
                    return Model.IMAGE_TYPE;

                default:

                    return -1;
            }
        }


    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void addData(Model dataViews) {
        this.dataSet.add(dataViews);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        final Model object = dataSet.get(listPosition);
        if (object != null) {
            switch (object.getType()) {

                case Model.IMAGE_TYPE:
                    final ImageTypeViewHolder imageTypeViewHolder = (ImageTypeViewHolder) holder;
                    imageTypeViewHolder.txt_likes.setText(String.valueOf(object.getLikes()) + " likes");
                    imageTypeViewHolder.txt_username.setText(String.valueOf(object.getUsername()));
                    imageTypeViewHolder.uid = object.getUuid();
                    imageTypeViewHolder.txtCaption.setText(object.getCaption());
//                    imageTypeViewHolder.txt_date.setText();

                    if (object.isRange()) {
                        imageTypeViewHolder.btn_range.setColorFilter(R.color.range_colors);
                        imageTypeViewHolder.btn_range.setImageResource(R.drawable.in_range);
                    } else {
                        imageTypeViewHolder.btn_range.setImageResource(R.drawable.out_range);
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    SimpleDateFormat formatterOut = new SimpleDateFormat("dd MMM yyyy");
                    String dateString;
                    try {
                        Date date = formatter.parse(object.getDate());
                         dateString = formatterOut.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        dateString = object.getDate();
                    }

                    imageTypeViewHolder.txt_date.setText(dateString);
                    imageTypeViewHolder.txt_username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("uid", imageTypeViewHolder.uid);
                            bundle.putBoolean("followed", true);

                            AppCompatActivity mainActivity = (MainActivity) mContext;
                            ProfileFragment profileFragment = new ProfileFragment();
                            profileFragment.setArguments(bundle);
                            mainActivity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_frame, profileFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });

                    imageTypeViewHolder.background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("uid", imageTypeViewHolder.uid);
                            bundle.putBoolean("followed", true);

                            AppCompatActivity mainActivity = (MainActivity) mContext;
                            ProfileFragment profileFragment = new ProfileFragment();
                            profileFragment.setArguments(bundle);
                            mainActivity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_frame, profileFragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });

                    imageTypeViewHolder.txt_likes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (object.getLikes() > 0) {
                                Intent intent = new Intent(mContext, FeedLikesActivity.class);
                                intent.putExtra("postid", object.getPostid());
                                intent.putExtra("username", object.getUsername());
                                mContext.startActivity(intent);
                            }

                        }
                    });


                    Glide.with(mContext).load(object.getImage()).into(imageTypeViewHolder.post_image);
                    if (object.getProfilepic() == null) {
                        FirebaseFirestore.getInstance().collection("Users").document(object.getUuid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        String image = task.getResult().getString("image");
                                        dataSet.get(listPosition).setProfilepic(image);
                                        Glide.with(mContext).load(object.getProfilepic()).into(imageTypeViewHolder.background);

                                    }

                                }
                            }
                        });
                    } else {
                        Glide.with(mContext).load(object.getProfilepic()).into(imageTypeViewHolder.background);
                    }
                    imageTypeViewHolder.btn_like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onItemClickListener.onItemClick(listPosition, 1);

                        }
                    });

                    imageTypeViewHolder.txt_comments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(mContext, FeedCommentsActivity.class);
                            intent.putExtra("postid", object.getPostid());
                            intent.putExtra("username", object.getUsername());
                            mContext.startActivity(intent);


                        }
                    });


                    CollectionReference citiesRef = FirebaseFirestore.getInstance().collection("likes");
                    Query query = citiesRef.whereEqualTo("postid", object.getPostid());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            Log.d("fromadpter", "Done");

                            if (task.isSuccessful()) {
                                Log.d("fromadpter", task.toString());
                                dataSet.get(listPosition).setLikes(task.getResult().size());
                                imageTypeViewHolder.txt_likes.setText(String.valueOf(object.getLikes()) + " likes");
                            }


                        }
                    });

                    break;
            }
        }

    }

    public void likeClicked(int pos) {
        dataSet.get(pos).setLikes(dataSet.get(pos).getLikes() + 1);
        notifyItemChanged(pos);
    }

    public void unlikeClicked(int pos) {
        dataSet.get(pos).setLikes(dataSet.get(pos).getLikes() - 1);
        notifyItemChanged(pos);
    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView post_image;
        TextView txt_likes;
        CircleImageView background;
        TextView txt_username;
        ImageView txt_comments;
        TextView txt_date;
        ImageView btn_like;
        ImageView btn_range;
        String uid;
        TextView txtCaption;


        public ImageTypeViewHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            post_image = itemView.findViewById(R.id.post_image);
            txt_likes = itemView.findViewById(R.id.txt_likes);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_username = itemView.findViewById(R.id.txt_username);
            btn_like = itemView.findViewById(R.id.btn_like);
            txt_comments = itemView.findViewById(R.id.button_comment);
            btn_range = itemView.findViewById(R.id.button_range);
            txtCaption = itemView.findViewById(R.id.txt_caption);
        }

    }
}
