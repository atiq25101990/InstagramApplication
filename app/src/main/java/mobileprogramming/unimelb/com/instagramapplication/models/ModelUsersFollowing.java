package mobileprogramming.unimelb.com.instagramapplication.models;


public class ModelUsersFollowing {
    private String uuid;
    private String followerid;
    private String date;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFollowDate() {
        return followDate;
    }

    public void setFollowDate(String followDate) {
        this.followDate = followDate;
    }

    private String followDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFollowerid() {
        return followerid;
    }

    public void setFollowerid(String followerid) {
        this.followerid = followerid;
    }

    public ModelUsersFollowing() {

    }
}
