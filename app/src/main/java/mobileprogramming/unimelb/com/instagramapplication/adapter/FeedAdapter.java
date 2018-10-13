package mobileprogramming.unimelb.com.instagramapplication.adapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import mobileprogramming.unimelb.com.instagramapplication.FeedCommentsActivity;
import mobileprogramming.unimelb.com.instagramapplication.FeedLikesActivity;
import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnItemClickListener;
import mobileprogramming.unimelb.com.instagramapplication.models.Model;
import mobileprogramming.unimelb.com.instagramapplication.utils.Constant;
import mobileprogramming.unimelb.com.instagramapplication.viewholder.LoadingHolder;


public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemClickListener onItemClickListener;
    private ArrayList<Model> dataSet;
    AppCompatActivity mContext;
    int total_types;
    MediaPlayer mPlayer;
    private boolean fabStateVolume = false;

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


    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView post_image;
        TextView txt_likes;
        TextView txt_username;
        TextView txt_comments;
        ImageView btn_like;
        ImageButton btnOption;

        public ImageTypeViewHolder(View itemView) {
            super(itemView);
            post_image = itemView.findViewById(R.id.post_image);
            txt_likes = itemView.findViewById(R.id.txt_likes);
            txt_username = itemView.findViewById(R.id.txt_username);
            txt_comments = itemView.findViewById(R.id.txt_comments);
            btn_like = itemView.findViewById(R.id.btn_like);
            btnOption = itemView.findViewById(R.id.btn_option);
        }

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
                    ImageTypeViewHolder imageTypeViewHolder = (ImageTypeViewHolder) holder;
                    imageTypeViewHolder.txt_likes.setText(String.valueOf(object.getLikes()) + " Total likes");
                    imageTypeViewHolder.txt_username.setText(String.valueOf(object.getUsername()));
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
                    imageTypeViewHolder.txt_comments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(mContext, FeedCommentsActivity.class);
                            intent.putExtra("postid", object.getPostid());
                            intent.putExtra("username", object.getUsername());
                            mContext.startActivity(intent);


                        }
                    });

                    Glide.with(mContext).load(object.getImage()).into(imageTypeViewHolder.post_image);
                    imageTypeViewHolder.btn_like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            object.setLikes(object.getLikes() + 1);
                            onItemClickListener.onItemClick(listPosition, 1);
                            notifyItemChanged(listPosition);
                        }
                    });
                    imageTypeViewHolder.btnOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onItemClickListener.onItemClick(listPosition, 2);
                        }
                    });
                    break;
            }
        }

    }


}
