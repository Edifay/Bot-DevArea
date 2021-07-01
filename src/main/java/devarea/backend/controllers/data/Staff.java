package devarea.backend.controllers.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Staff {

    @JsonProperty("name")
    private String name;
    @JsonProperty("bio")
    private String bio;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("id")
    private String id;
    @JsonProperty("urlAvatar")
    private String urlAvatar;

    public Staff() {

    }

    public Staff(final String name, final String bio, final String id) {
        this.name = name;
        this.bio = bio;
        this.id = id;
    }

    public Staff(final String name, final String bio, final String id, final String url) {
        this.name = name;
        this.bio = bio;
        this.id = id;
        this.urlAvatar = url;
    }

    public String getName() {
        return this.name;
    }

    public String getBio() {
        return this.bio;
    }

    public String getId() {
        return this.id;
    }

    @JsonIgnore
    public void resetId() {
        this.id = null;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public String getUrlAvatar() {
        return this.urlAvatar;
    }


}
