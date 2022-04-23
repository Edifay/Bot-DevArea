package devarea.backend.controllers.tools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebReseau {

    @JsonProperty("name")
    protected String name;
    @JsonProperty("url")
    protected String url;
    @JsonProperty("description")
    protected String description;
    @JsonProperty("index")
    protected int index;

    public WebReseau() {

    }

    public WebReseau(final String name, final String url, final String description, final int index) {
        this.name = name;
        this.url = url;
        this.description = description;
        this.index = index;
    }

    @JsonIgnore
    public String getName() {
        return this.name;
    }

    @JsonIgnore
    public String getUrl() {
        return this.url;
    }

    @JsonIgnore
    public String getDescription() {
        return this.description;
    }

    @JsonIgnore
    public int getIndex() {
        return this.index;
    }

}
