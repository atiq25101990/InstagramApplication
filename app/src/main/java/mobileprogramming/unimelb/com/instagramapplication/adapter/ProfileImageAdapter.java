package mobileprogramming.unimelb.com.instagramapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

import mobileprogramming.unimelb.com.instagramapplication.PostActivity;

public class ProfileImageAdapter extends BaseAdapter {
    private HashMap<String, String> profile = new HashMap<>();
    private Context mContext;
    private ArrayList<String> urlList;
    private static final String TAG = "ProfileImageAdapter";


    public void setUrlList(ArrayList<String> urlList) {
        this.urlList = urlList;
    }

    public ProfileImageAdapter(Context context, HashMap<String, String> profile) {
        this.mContext = context;
        this.profile = profile;
    }

    @Override
    public int getCount() {
        return urlList.size();
    }

    @Override
    public Object getItem(int position) {
        return urlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            Glide.with(mContext).load(urlList.get(position)).into(imageView);
            final int pos = position;
            imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PostActivity.class);
                    intent.putExtra("postid", urlList.get(pos));
                    intent.putExtra("profileURL", profile.get("imageURL"));
                    intent.putExtra("profileUsername", profile.get("username"));
                    mContext.startActivity(intent);
                }
            });
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }
        Log.d(TAG, "getView: Setting new Image: " + position);
        return imageView;
    }
}
