package mobileprogramming.unimelb.com.instagramapplication.models;

public class InRangePhoto {

    private String done_for_id;
    private String done_by_id;
    private String image;
    private String date;
    private String username;


    public InRangePhoto(String done_for_id, String done_by_id, String image, String date, String username) {
        this.done_for_id = done_for_id;
        this.done_by_id = done_by_id;
        this.image = image;
        this.date = date;
        this.username = username;
    }

    public InRangePhoto() {

    }

    public String getDone_for_id() {
        return done_for_id;
    }

    public void setDone_for_id(String done_for_id) {
        this.done_for_id = done_for_id;
    }

    public String getDone_by_id() {
        return done_by_id;
    }

    public void setDone_by_id(String done_by_id) {
        this.done_by_id = done_by_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
