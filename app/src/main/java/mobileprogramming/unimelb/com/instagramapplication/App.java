package mobileprogramming.unimelb.com.instagramapplication;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import mobileprogramming.unimelb.com.instagramapplication.utils.SessionManagers;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
        new SessionManagers(this);
    }
}
