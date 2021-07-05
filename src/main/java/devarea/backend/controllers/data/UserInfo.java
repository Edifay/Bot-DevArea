package devarea.backend.controllers.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dependencies.auth.domain.User;
import dependencies.auth.main.OAuthBuilder;
import devarea.backend.controllers.rest.ControllerOAuth2;
import devarea.bot.Init;
import devarea.bot.automatical.XpCount;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;

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
        this.lastTimeFetch = 0;
    }

    public UserInfo(OAuthBuilder builder) {
        this.builder = builder;
        this.lastTimeFetch = 0;
        this.id = this.builder.getIdUser();
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

    @JsonIgnore
    public Snowflake getAsSnowflake() {
        return Snowflake.of(this.id);
    }

    public boolean needToFetch() {
        return System.currentTimeMillis() - this.lastTimeFetch > 600000L;
    }

    @JsonIgnore
    private boolean fetch() {
        this.isMember = ControllerOAuth2.isMember(this.id);
        System.out.println("Fetch is member ! " + this.id + " isMember : " + this.isMember);
        if (this.isMember) {
            System.out.println("Member fetched with bot !");
            Member member = Init.devarea.getMemberById(getAsSnowflake()).block();

            this.setId(member.getId().asString());
            this.setName(member.getUsername());
            this.setUrlAvatar(member.getAvatarUrl());
            if (this.urlAvatar == null)
                this.setUrlAvatar(member.getDefaultAvatarUrl());
            this.lastTimeFetch = System.currentTimeMillis();
            this.setRank(XpCount.getRankOf(getAsSnowflake()));
            this.setXp(XpCount.getXpOf(getAsSnowflake()));

            return true;
        } else if (builder != null) {
            System.out.println("Fetched with builder !");
            User user = builder.getUser();

            this.setId(user.getId());
            this.setName(user.getUsername());
            this.setUrlAvatar(user.getAvatar());
            if (this.urlAvatar == null) {
                this.setUrlAvatar("https://discord.com/assets/2d20a45d79110dc5bf947137e9d99b66.svg");
            }
            this.lastTimeFetch = System.currentTimeMillis();

            return true;
        }
        return false;
    }

    public boolean canBeFetch() {
        this.isMember = ControllerOAuth2.isMember(this.id);
        return this.isMember || builder != null;
    }

    public boolean verifFetchNeeded(final boolean force) {
        if (force || needToFetch()) {
            fetch();
            return true;
        }
        return false;
    }

    public void setBuilder(OAuthBuilder builder) {
        this.builder = builder;
    }
}
