package mobileprogramming.unimelb.com.instagramapplication.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import mobileprogramming.unimelb.com.instagramapplication.R;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context c;
    ArrayList<String> devices;

    public BluetoothDeviceAdapter(Context c, ArrayList<String> devices) {
        this.c = c;
        this.devices = devices;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.item_bluetooth,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).device.setText(devices.get(position));
    }


    @Override
    public int getItemCount() {
        return devices.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView device;


        public MyViewHolder(final View itemView) {
            super(itemView);
            device = itemView.findViewById(R.id.txt_device);
        }

    }
}
