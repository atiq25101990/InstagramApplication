package mobileprogramming.unimelb.com.instagramapplication.Share;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mobileprogramming.unimelb.com.instagramapplication.PhotoEditingActivity;
import mobileprogramming.unimelb.com.instagramapplication.R;
import mobileprogramming.unimelb.com.instagramapplication.utils.Permissions;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment{

    private String type;

    public static String getTAG() {
        return TAG;
    }

    private static final String TAG = "PhotoFragment";
    //constant
    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int GALLERY_FRAGMENT_NUM = 2;
    private static final int CAMERA_REQUEST_CODE = 5;

    private String[] inRange;

    public PhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public void setArguments(@Nullable Bundle bundle) {
        type = bundle.getString("type");

        assert type != null;
        if(type.equals("inrange")){
            inRange = bundle.getStringArray("users");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        Log.d(TAG, "onCreateView: started.");

        Button btnLaunchCamera = view.findViewById(R.id.btnLaunchCamera);
        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick Launching Camera");

                if(((ShareActivity)getActivity()).getCurrentTabNumber() == PHOTO_FRAGMENT_NUM){
                    if(((ShareActivity)getActivity()).checkPermissions(Permissions.CAMERA_PERMISSION[0])){
                        Log.d(TAG, "onClick: starting camera");
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
                    }else{
                        Intent intent = new Intent(getActivity(), ShareActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("type", type);
                        if(type.equals("inrange")){
                            intent.putExtra("users", inRange);
                        }
                        startActivity(intent);
                    }
                }
            }
        });

        return view;
    }

    private boolean isRootTask(){
        if(((ShareActivity)getActivity()).getTask() == 0){
            return true;
        }else {
            return false;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: Finish capturing a photo");
            Log.d(TAG, "onActivityResult: Attempting to go to final screen");

            //navigate to the final share screen to publish photo

            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");

            if(isRootTask()){
                try{
                    Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
                    Intent intent = new Intent(getActivity(), PhotoEditingActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                }
            }

        }
    }
}
