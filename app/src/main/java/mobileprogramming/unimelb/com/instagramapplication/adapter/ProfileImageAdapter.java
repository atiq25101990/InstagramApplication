package mobileprogramming.unimelb.com.instagramapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProfileImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> urlList;
    private static final String TAG = "ProfileImageAdapter";


    public void setUrlList(ArrayList<String> urlList) {
        this.urlList = urlList;
    }

    public ProfileImageAdapter(Context context) {
        mContext = context;
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
//            imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 300));

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }
        Log.d(TAG, "getView: Setting new Image: " + position);
        return imageView;
    }
}
