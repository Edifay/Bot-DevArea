package devarea.backend.controllers.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dependencies.auth.main.OAuthBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo {

    @JsonProperty
    private boolean isMember;
    @JsonProperty
    private String id;
    @JsonProperty
    private String urlAvatar;
    @JsonProperty
    private String name;
    @JsonProperty
    private int rank;
    @JsonProperty
    private int xp;

    @JsonIgnore
    private OAuthBuilder builder;

    @JsonIgnore
    private long lastTimeFetch;

    public UserInfo() {

    }

    public UserInfo(OAuthBuilder builder) {
        this.builder = builder;
    }

    @JsonIgnore
    public String getUrlAvatar() {
        return urlAvatar;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public long getLastTimeFetch() {
        return lastTimeFetch;
    }

    @JsonIgnore
    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public void setLastTimeFetch(long lastTimeFetch) {
        this.lastTimeFetch = lastTimeFetch;
    }

    @JsonIgnore
    public void setMember(boolean member) {
        isMember = member;
    }

    @JsonIgnore
    public boolean isMember() {
        return this.isMember;
    }

    @JsonIgnore
    public void setRank(int rank) {
        this.rank = rank;
    }

    @JsonIgnore
    public void setXp(int xp) {
        this.xp = xp;
    }

    @JsonIgnore
    public int getRank() {
        return rank;
    }

    @JsonIgnore
    public int getXp() {
        return xp;
    }

    @JsonIgnore
    public OAuthBuilder getBuilder() {
        return builder;
    }
}
