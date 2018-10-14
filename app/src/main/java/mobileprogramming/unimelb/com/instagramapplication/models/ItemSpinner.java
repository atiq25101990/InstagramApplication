package mobileprogramming.unimelb.com.instagramapplication.models;

public class ItemSpinner {

    private String name;
    private Integer id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public ItemSpinner(Integer id, String name) {

        this.name = name;
        this.id = id;


    }


}
