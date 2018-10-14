package mobileprogramming.unimelb.com.instagramapplication.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.models.ItemSpinner;

public class SpinnerAdapter extends ArrayAdapter<ItemSpinner> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ItemSpinner> data = new ArrayList<>();

    public SpinnerAdapter(Context context, int layoutResourceId, ArrayList<ItemSpinner> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;

        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.name = (TextView) row.findViewById(R.id.textView_name);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        final ItemSpinner item = data.get(position);
        holder.name.setText(item.getName());

        return row;

    }

    static class RecordHolder {
        TextView name;


    }

}