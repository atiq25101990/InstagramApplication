package mobileprogramming.unimelb.com.instagramapplication.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.adapter.SpinnerAdapter;
import mobileprogramming.unimelb.com.instagramapplication.listener.OnItemSelectedListener;
import mobileprogramming.unimelb.com.instagramapplication.models.ItemSpinner;


@SuppressLint("ValidFragment")
public class CustomeDialogFragment extends android.support.v4.app.DialogFragment {

    OnItemSelectedListener itemSelectedListener;
    ListView lv;
    SpinnerAdapter adapter;
    ArrayList<ItemSpinner> itemSpinners = new ArrayList<>();
    private Context context;
    private int layout;
    private View view;
    private String title;
    private TextView txtTitle, btnCancle;
    private boolean isTitle;

    @SuppressLint("ValidFragment")
    public CustomeDialogFragment(Context context, int layout, boolean isTitle, ArrayList<ItemSpinner> itemSpinners) {
        this.isTitle = isTitle;
        this.context = context;
        this.layout = layout;
        this.itemSpinners = itemSpinners;

    }

    @SuppressLint("ValidFragment")
    public CustomeDialogFragment(Context context, int layout, boolean isTitle, String title, ArrayList<ItemSpinner> itemSpinners) {
        this.isTitle = isTitle;
        this.title = title;
        this.context = context;
        this.layout = layout;
        this.itemSpinners = itemSpinners;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(layout, null);
        intView();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemSelectedListener.onItemSelected(itemSpinners.get(i));
                dismiss();
            }
        });


        return view;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    private void intView() {
        lv = (ListView) view.findViewById(R.id.listView1);
        adapter = new SpinnerAdapter(context, R.layout.item_spinner_lang, itemSpinners);

        if (isTitle == true) {

            txtTitle = (TextView) view.findViewById(R.id.txt_title);
            txtTitle.setText(title);
            btnCancle = (TextView) view.findViewById(R.id.btn_cancle);
            btnCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

        }

    }

    public void setItemSelectedListener(OnItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }
}
