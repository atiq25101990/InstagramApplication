package mobileprogramming.unimelb.com.instagramapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.models.HomeFeedModel;

public class HomeFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<HomeFeedModel> notificationList = new ArrayList<>();

    public HomeFeedAdapter(Context mContext, List<HomeFeedModel> categoryList) {
        this.mContext = mContext;
        notificationList = categoryList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.item_feed, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
        }

    }

    @Override
    public int getItemViewType(int position) {

        return 1;

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View view) {
            super(view);


        }

    }
}