package mobileprogramming.unimelb.com.instagramapplication.ActivityFeed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mobileprogramming.unimelb.com.instagramapplication.R;

public class ActivityFeed extends AppCompatActivity {

    private static final String TAG = "ActivityFeed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
    }
}
