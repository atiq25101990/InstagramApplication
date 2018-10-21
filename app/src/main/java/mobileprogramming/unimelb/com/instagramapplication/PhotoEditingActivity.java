package mobileprogramming.unimelb.com.instagramapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.effect.EffectFactory;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

import ja.burhanrashid52.photoeditor.CustomEffect;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import mobileprogramming.unimelb.com.instagramapplication.Share.NextActivity;
import mobileprogramming.unimelb.com.instagramapplication.utils.CommonUtils;
import mobileprogramming.unimelb.com.instagramapplication.utils.UniversalImageLoader;

public class PhotoEditingActivity extends AppCompatActivity {

    private PhotoEditorView editImage;
    private String imgUrl;
    private static final String TAG = "PhotoEditingActivity";

    private String mAppend = "file:/";
    private Bitmap bitmap;
    private PhotoEditor mPhotoEditor;
    private SeekBar brightnessButton;
    private SeekBar contrastButton;

    private ImageButton sepiaButton;
    private ImageButton bwButton;
    private ImageButton vignetteButton;
    private Button nextButton;
    private String type;
    private String[] inRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editing);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        assert type != null;
        if(type.equals("inRange")){
            inRange = intent.getStringArrayExtra("users");
        }
        setImage();
    }


    private void setImage() {
        Intent receivedIntent = getIntent();
        editImage = findViewById(R.id.photoEditorView);
        brightnessButton = findViewById(R.id.brightnessButton);
        contrastButton = findViewById(R.id.contrastButton);
        sepiaButton = findViewById(R.id.sepiaFilter);
        bwButton = findViewById(R.id.bwFilter);
        vignetteButton = findViewById(R.id.vignetteFilter);
        nextButton = findViewById(R.id.filterDoneButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showLoadingDialog(PhotoEditingActivity.this);
                mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
                    @Override
                    public void onBitmapReady(Bitmap saveBitmap) {
                        Intent intent = new Intent(PhotoEditingActivity.this, CropImageActivity.class);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        intent.putExtra(getString(R.string.selected_bitmap), byteArray);
                        intent.putExtra("type", type);
                        if(type.equals("inRange")){
                            intent.putExtra("users", inRange);
                        }

                        CommonUtils.dismissProgressDialog();
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }
        });


        final CustomEffect.Builder brightnessEffect = new CustomEffect.Builder(EffectFactory.EFFECT_BRIGHTNESS);
        final CustomEffect.Builder contrastEffect = new CustomEffect.Builder(EffectFactory.EFFECT_CONTRAST);

        sepiaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.setFilterEffect(PhotoFilter.SEPIA);
            }
        });

        bwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.setFilterEffect(PhotoFilter.DOCUMENTARY);
            }
        });

        vignetteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.setFilterEffect(PhotoFilter.TINT);
            }
        });


        brightnessButton.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPhotoEditor.setFilterEffect(brightnessEffect.setParameter("brightness", progress/100f).build());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        contrastButton.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPhotoEditor.setFilterEffect(contrastEffect.setParameter("contrast", progress/100f).build());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (receivedIntent.hasExtra(getString(R.string.selected_image))) {
            imgUrl = receivedIntent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new image: " + imgUrl);
            UniversalImageLoader.setImage(imgUrl, editImage.getSource(), null, mAppend);
        } else if (receivedIntent.hasExtra(getString(R.string.selected_bitmap))) {
            bitmap = (Bitmap) receivedIntent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new bitmap");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Glide.with(getApplicationContext())
                    .load(stream.toByteArray())
                    .into(editImage.getSource());
        }




        mPhotoEditor = new PhotoEditor.Builder(this, editImage)
                .setPinchTextScalable(true)
                .build();
    }
}
