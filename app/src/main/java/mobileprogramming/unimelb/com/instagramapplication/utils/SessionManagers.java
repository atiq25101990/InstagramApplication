package mobileprogramming.unimelb.com.instagramapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManagers {
    private static final String PREF_NAME = "insta_pref";

    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_KEY_LANG = "slang";
    private static SessionManagers ourInstance;
    private static Context _context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private int PRIVATE_MODE = 0;

    public SessionManagers(Context context) {
        _context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static SessionManagers getInstance() {
        if (ourInstance == null) {
            ourInstance = new SessionManagers(_context);
        }
        return ourInstance;
    }


    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(Constant.KEY_NAME, pref.getString(Constant.KEY_NAME,null));
        user.put(Constant.KEY_UNAME, pref.getString(Constant.KEY_UNAME,null));
        return user;
    }

    public void setUserProfile(HashMap<String, String> userProfile) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(Constant.KEY_USERID, userProfile.get(Constant.KEY_USERID));
        editor.putString(Constant.KEY_EMAIL, userProfile.get(Constant.KEY_EMAIL));
        editor.putString(Constant.KEY_NAME, userProfile.get(Constant.KEY_NAME));
        editor.putString(Constant.KEY_UNAME, userProfile.get(Constant.KEY_UNAME));
        editor.commit();
    }

    public boolean IsLogin() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser(final Context context) {
        editor.clear();
        editor.commit();
    }
}
