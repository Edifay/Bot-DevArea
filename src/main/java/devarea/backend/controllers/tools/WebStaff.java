package devarea.backend.controllers.tools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebStaff {

    @JsonProperty("name")
    private String name;
    @JsonProperty("bio")
    private String bio;
    @JsonProperty("urlAvatar")
    private String urlAvatar;
    @JsonProperty("idCss")
    private String idCss;

    @JsonIgnore
    private String id;

    public WebStaff() {
    }

    public WebStaff(final String name, final String bio, final String id) {
        this.name = name;
        this.bio = bio;
        this.id = id;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonIgnore
    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    @JsonIgnore
    public void setIdCss(String idCss) {
        this.idCss = idCss;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

}
