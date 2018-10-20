package mobileprogramming.unimelb.com.instagramapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import mobileprogramming.unimelb.com.instagramapplication.utils.UniversalImageLoader;

public class PhotoEditingActivity extends AppCompatActivity {

    private PhotoEditorView editImage;
    private String imgUrl;
    private static final String TAG = "PhotoEditingActivity";

    private String mAppend = "file:/";
    private Bitmap bitmap;
    private PhotoEditor mPhotoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editing);

        setImage();
    }


    private void setImage() {
        Intent intent = getIntent();
        editImage = findViewById(R.id.photoEditorView);

        if (intent.hasExtra(getString(R.string.selected_image))) {
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new image: " + imgUrl);
            UniversalImageLoader.setImage(imgUrl, editImage.getSource(), null, mAppend);
        } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new bitmap");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Glide.with(getApplicationContext())
                    .load(stream.toByteArray())
                    .into(editImage.getSource());
        }




        mPhotoEditor = new PhotoEditor.Builder(this, editImage)
                .setPinchTextScalable(true)
                .build();

        mPhotoEditor.setBrushDrawingMode(true);
    }
}
