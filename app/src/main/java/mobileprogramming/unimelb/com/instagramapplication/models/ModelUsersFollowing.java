package mobileprogramming.unimelb.com.instagramapplication.models;


public class ModelUsersFollowing {
    private String uuid;
    private String followerid;

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
