package mobileprogramming.unimelb.com.instagramapplication.models;

public class Photo {

    private String caption;
    private String date;
    private String image;
    private String photo_id;
    private String uid;
    private String tags;
    private String username;
    private String location;

    public Photo(){}

    public Photo(String caption, String date, String image_path, String photo_id, String user_id, String tags) {
        this.caption = caption;
        this.date = date;
        this.image = image;
        this.photo_id = photo_id;
        this.uid = uid;
        this.tags = tags;
        this.username = username;
    }



    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "caption='" + caption + '\'' +
                ", date='" + date + '\'' +
                ", image='" + image + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", location='" + location + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }


}
