package mobileprogramming.unimelb.com.instagramapplication.models;

import android.util.Log;

import java.util.Objects;

public class ModelActivity {

    private static final String TAG = "ModelActivity";
    private String doneForID;
    private String doneForName;
    private String doneByID;
    private String doneByName;
    private String date;
    private String type;
    private String comment;
    private String postid;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof ModelActivity))
            return false;
        if (obj == this)
            return true;

        Log.d(TAG, "equals: Checking!");
        ModelActivity modelActivity = (ModelActivity) obj;
        return Objects.equals(doneByID, modelActivity.getDoneByID()) &&
                Objects.equals(doneByName, modelActivity.getDoneByName()) &&
                Objects.equals(doneForID, modelActivity.getDoneForID()) &&
                Objects.equals(doneByName, modelActivity.getDoneForName()) &&
                Objects.equals(date, modelActivity.getDate()) &&
                Objects.equals(postid, modelActivity.getPostid()) &&
                Objects.equals(comment, modelActivity.getComment()) &&
                Objects.equals(type, modelActivity.getType());
    }

    @Override
    public int hashCode() {
        Log.d(TAG, "hashCode: Hashing ");
        // static hash to force equality check
        return 31;
    }

    public String getDoneForID() {
        return doneForID;
    }

    public void setDoneForID(String doneForID) {
        this.doneForID = doneForID;
    }

    public String getDoneForName() {
        return doneForName;
    }

    public void setDoneForName(String doneForName) {
        this.doneForName = doneForName;
    }

    public String getDoneByID() {
        return doneByID;
    }

    public void setDoneByID(String doneByID) {
        this.doneByID = doneByID;
    }

    public String getDoneByName() {
        return doneByName;
    }

    public void setDoneByName(String doneByName) {
        this.doneByName = doneByName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }
}
