package mobileprogramming.unimelb.com.instagramapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

import mobileprogramming.unimelb.com.instagramapplication.Share.NextActivity;
import mobileprogramming.unimelb.com.instagramapplication.utils.CommonUtils;

public class CropImageActivity extends AppCompatActivity {



//    @BindView(R.id.cropImageView);
    CropImageView mCropImageView;
    String type;
    String[] inRange;
    Button submitButton;
    android.support.v7.widget.Toolbar mToolbar;
    private Bitmap bitmap;
    private static final String TAG = "CropImageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        mToolbar = findViewById(R.id.cropToolbar);
        mCropImageView = findViewById(R.id.pictureCrop);
        submitButton = findViewById(R.id.submit_button);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent thisIntent = getIntent();
        type = thisIntent.getStringExtra("type");

        if(type.equals("inRange")){
            inRange = thisIntent.getStringArrayExtra("users");
        }

        byte[] byteArray = thisIntent.getByteArrayExtra(getString(R.string.selected_bitmap));
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Log.d(TAG, "setImage: got new bitmap");

        mCropImageView.setImageBitmap(bitmap);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showLoadingDialog(CropImageActivity.this);
                mCropImageView.getCroppedImageAsync();
            }
        });


        mCropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                Intent intent = new Intent(CropImageActivity.this, NextActivity.class);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                result.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra(getString(R.string.selected_bitmap), byteArray);
                intent.putExtra("type", type);
                if(type.equals("inRange")){
                    intent.putExtra("users", inRange);
                }
                CommonUtils.dismissProgressDialog();
                startActivity(intent);
            }
        });
    }
}
