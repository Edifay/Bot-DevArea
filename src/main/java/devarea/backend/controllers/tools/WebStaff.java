package devarea.backend.controllers.tools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("id")
    private String id;

    public WebStaff() {

    }

    public WebStaff(final String name, final String bio, final String id) {
        this.name = name;
        this.bio = bio;
        this.id = id;
    }

    public WebStaff(final String name, final String bio, final String id, final String url) {
        this.name = name;
        this.bio = bio;
        this.id = id;
        this.urlAvatar = url;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public void resetId() {
        this.id = null;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }


    public void setIdCss(String idCss) {
        this.idCss = idCss;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "WebStaff{" +
                "name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", id='" + id + '\'' +
                ", urlAvatar='" + urlAvatar + '\'' +
                ", idCss='" + idCss + '\'' +
                '}';
    }

    @Override
    public WebStaff clone() {
        WebStaff cloned = new WebStaff();
        cloned.name = name;
        cloned.bio = bio;
        cloned.id = id;
        cloned.urlAvatar = urlAvatar;
        cloned.idCss = idCss;

        return cloned;
    }

}
