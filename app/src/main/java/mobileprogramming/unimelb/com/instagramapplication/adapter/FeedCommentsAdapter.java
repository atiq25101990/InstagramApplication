package mobileprogramming.unimelb.com.instagramapplication.adapter;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnItemClickListener;
import mobileprogramming.unimelb.com.instagramapplication.models.Model;
import mobileprogramming.unimelb.com.instagramapplication.models.ModelLikes;
import mobileprogramming.unimelb.com.instagramapplication.utils.Constant;
import mobileprogramming.unimelb.com.instagramapplication.viewholder.LoadingHolder;


public class FeedCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    AppCompatActivity mContext;
    int total_types;
    private OnItemClickListener onItemClickListener;
    private ArrayList<ModelLikes> dataSet = new ArrayList<>();

    public FeedCommentsAdapter(AppCompatActivity context, ArrayList<ModelLikes> data) {
        this.dataSet = data;
        this.mContext = context;
        total_types = dataSet.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Model.IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comments, parent, false);
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

    public void addData(ModelLikes dataViews) {
        this.dataSet.add(dataViews);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        final ModelLikes object = dataSet.get(listPosition);
        if (object != null) {
            switch (object.getType()) {
                case Model.IMAGE_TYPE:
                    final ImageTypeViewHolder imageTypeViewHolder = (ImageTypeViewHolder) holder;
                    imageTypeViewHolder.txt_username.setText(String.valueOf(object.getUsername()));
                    imageTypeViewHolder.txt_text.setText(String.valueOf(object.getText()));
                    if (object.getProfilepic() == null) {
                        FirebaseFirestore.getInstance().collection("Users").document(object.getUuid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        String image = task.getResult().getString("image");
                                        dataSet.get(listPosition).setProfilepic(image);
                                        Glide.with(mContext).load(object.getProfilepic()).into(imageTypeViewHolder.background);
//                                        name = task.getResult().getString("name");
//                                        username = task.getResult().getString("username");
//                                        String bio = task.getResult().getString("bio");

                                    }

                                }
                            }
                        });
                    } else {
                        Glide.with(mContext).load(object.getProfilepic()).into(imageTypeViewHolder.background);
                    }

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
        TextView txt_text;
        CircleImageView background;

        public ImageTypeViewHolder(View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_username);
            background = itemView.findViewById(R.id.background);
            txt_text = itemView.findViewById(R.id.txt_text);
        }

    }


}
