package net.goeasyway.uploadimage.model;

/**
 * Created by lan on 17/4/9.
 */

public class Photo {

    private String name;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "{name: "+ name + ", url: " + url + "}";
    }
}
