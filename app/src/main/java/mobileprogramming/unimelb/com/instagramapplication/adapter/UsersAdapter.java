package mobileprogramming.unimelb.com.instagramapplication.adapter;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnItemClickListener;
import mobileprogramming.unimelb.com.instagramapplication.models.Model;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelUsers;
import mobileprogramming.unimelb.com.instagramapplication.utils.Constant;
import mobileprogramming.unimelb.com.instagramapplication.viewholder.LoadingHolder;


public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    AppCompatActivity mContext;
    int total_types;
    private OnItemClickListener onItemClickListener;
    private ArrayList<ModelUsers> dataSet;

    public UsersAdapter(AppCompatActivity context, ArrayList<ModelUsers> data) {
        this.dataSet = data;
        this.mContext = context;
        total_types = dataSet.size();
    }

    public void followed(int pos) {
        dataSet.get(pos).setFollwing(true);
        notifyItemChanged(pos);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Model.IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users, parent, false);
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

    public void addData(ModelUsers dataViews) {
        this.dataSet.add(dataViews);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        final ModelUsers object = dataSet.get(listPosition);
        if (object != null) {
            switch (object.getType()) {
                case Model.IMAGE_TYPE:
                    ImageTypeViewHolder imageTypeViewHolder = (ImageTypeViewHolder) holder;
                    imageTypeViewHolder.txt_username.setText(String.valueOf(object.getUsername()));
                    Glide.with(mContext).load(object.getImage()).into(imageTypeViewHolder.background);
                    if (!object.isFollwing()) {
                        imageTypeViewHolder.btn_follow.setText("Follow");

                    } else {
                        imageTypeViewHolder.btn_follow.setText("Following");


                    }

                    imageTypeViewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (object.isFollwing()) {
                                onItemClickListener.onItemClick(listPosition, 2);
                            } else {
                                onItemClickListener.onItemClick(listPosition, 1);
                            }
                        }
                    });
                    break;
            }
        }

    }

    public static class TextTypeViewHolder extends RecyclerView.ViewHolder {

        public TextTypeViewHolder(View itemView) {
            super(itemView);


        }

    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {

        TextView txt_username;
        CircleImageView background;
        Button btn_follow;

        public ImageTypeViewHolder(View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_username);
            background = itemView.findViewById(R.id.background);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }

    }


}
