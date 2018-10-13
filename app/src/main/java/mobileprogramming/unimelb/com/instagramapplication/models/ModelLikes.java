package mobileprogramming.unimelb.com.instagramapplication.models;


public class ModelLikes {

    public static final int TYPE_HEADER = 0;
    public static final int TEXT_TYPE = 4;
    public static final int IMAGE_TYPE = 1;
    public static final int IMAGE_TYPE3 = 3;
    public static final int AUDIO_TYPE = 2;
    public static final int TYPE_START_FIRST = 5;

    private int type=1;
    private String uuid;
    private String date;
    private String text;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    public ModelLikes() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static int getTypeHeader() {
        return TYPE_HEADER;
    }

    public static int getTextType() {
        return TEXT_TYPE;
    }

    public static int getImageType() {
        return IMAGE_TYPE;
    }

    public static int getImageType3() {
        return IMAGE_TYPE3;
    }

    public static int getAudioType() {
        return AUDIO_TYPE;
    }

    public static int getTypeStartFirst() {
        return TYPE_START_FIRST;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
