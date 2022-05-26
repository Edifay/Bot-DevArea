package devarea.backend.controllers.tools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.bot.automatical.XPHandler;

import java.util.Objects;

public class WebXPMember {

    @JsonProperty("id")
    protected String id;
    @JsonProperty("name")
    protected String name;
    @JsonProperty("xp")
    protected int xp;
    @JsonProperty("rank")
    protected int rank;
    @JsonProperty("level")
    protected int level;
    @JsonProperty("urlAvatar")
    protected String urlAvatar;
    @JsonIgnore
    protected long lastTimeFetch;

    public WebXPMember(final String id, final int xp, final int rank) {
        this.id = id;
        this.xp = xp;
        this.rank = rank;
        this.level = XPHandler.getLevelForXp(xp);
    }

    @JsonIgnore
    public long getLastTimeFetch() {
        return this.lastTimeFetch;
    }

    @JsonIgnore
    public void setLastTimeFetch(long lastTimeFetch) {
        this.lastTimeFetch = lastTimeFetch;
    }

    @JsonIgnore
    public int getXp() {
        return xp;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonIgnore
    public void setXp(int xp) {
        this.xp = xp;
    }

    @JsonIgnore
    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public void setRank(int rank) {
        this.rank = rank;
    }

    @JsonIgnore
    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    @JsonIgnore
    public int getRank() {
        return rank;
    }

    @JsonIgnore
    public String getUrlAvatar() {
        return urlAvatar;
    }

    @JsonIgnore
    public int getLevel() {
        return level;
    }

    @JsonIgnore
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebXPMember xpMember = (WebXPMember) o;
        return Objects.equals(id, xpMember.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
