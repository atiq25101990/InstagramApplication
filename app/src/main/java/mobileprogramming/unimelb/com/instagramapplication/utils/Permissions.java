package mobileprogramming.unimelb.com.instagramapplication.utils;

import android.support.v7.app.AppCompatActivity;
import java.lang.Object;
import android.Manifest;

public class Permissions extends AppCompatActivity{

    public static final String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    public static final String[] CAMERA_PERMISSION = {
            android.Manifest.permission.CAMERA
    };

    public static final String[] WRITE_STORAGE_PERMISSION = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String[] READ_STORAGE_PERMISSION = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE

    };

}
