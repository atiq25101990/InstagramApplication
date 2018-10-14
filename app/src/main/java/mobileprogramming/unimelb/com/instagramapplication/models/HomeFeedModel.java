package mobileprogramming.unimelb.com.instagramapplication.models;


public class HomeFeedModel {

    private String id;
    private String message;
    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public HomeFeedModel() {

    }
    public HomeFeedModel(String message) {
        this.message = message;

    }
    public HomeFeedModel(String id, String message, String date) {

        this.id = id;
        this.message = message;
        this.date = date;
    }

    @Override
    public String toString() {
        return id + "-" + message + "-" + date;
    }
}
