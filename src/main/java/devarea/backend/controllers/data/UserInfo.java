package devarea.backend.controllers.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dependencies.auth.domain.User;
import dependencies.auth.main.OAuthBuilder;
import devarea.backend.controllers.rest.ControllerMissions;
import devarea.backend.controllers.rest.ControllerOAuth2;
import devarea.bot.Init;
import devarea.bot.automatical.MissionsManager;
import devarea.bot.automatical.XpCount;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;

import java.util.ArrayList;

import static devarea.bot.Init.membersId;

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
    @JsonProperty
    protected int previous_xp_level;
    @JsonProperty
    protected int next_xp_level;
    @JsonProperty
    protected int level;
    @JsonProperty
    protected MissionForWeb[] missions_list;
    @JsonProperty
    protected String tag;

    @JsonIgnore
    private OAuthBuilder builder;

    @JsonIgnore
    private long lastTimeFetch;

    public UserInfo() {
        this.lastTimeFetch = 0;
    }

    public UserInfo(String id) {
        this.lastTimeFetch = 0;
        this.id = id;
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
        this.setLevel(XpCount.getLevelForXp(this.xp));
        this.setPrevious_xp_level(XpCount.getAmountForLevel(this.level));
        this.setNext_xp_level(XpCount.getAmountForLevel(this.level + 1));
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
        if (this.isMember) { // Si c'est un membre du serveur
            Member member = Init.devarea.getMemberById(getAsSnowflake()).block();
            this.setId(member.getId().asString());
            this.setName(member.getUsername());
            this.tag = member.getTag();
            this.setUrlAvatar(member.getAvatarUrl());
            if (this.urlAvatar == null)
                this.setUrlAvatar(member.getDefaultAvatarUrl());
            this.lastTimeFetch = System.currentTimeMillis();

            return true;
        } else if (builder != null) { // SI ce n'est pas un membre du serveur

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


    @JsonIgnore
    private void short_fetch() {
        this.isMember = ControllerOAuth2.isMember(this.id);
        if (this.isMember) { // Si c'est un membre du serveur
            this.missions_list =
                    ControllerMissions.transformMissionListToMissionForWebList(MissionsManager.getOf(Snowflake.of(this.id)))
                            .toArray(new MissionForWeb[0]);

            if (XpCount.haveBeenSet(getAsSnowflake())) {
                this.setRank(XpCount.getRankOf(getAsSnowflake()));
                this.setXp(XpCount.getXpOf(getAsSnowflake()));
            } else {
                this.setRank(XpCount.getRankOf(getAsSnowflake()));// may be set to last !
                this.setXp(0);
            }
        }
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
        short_fetch();
        return false;
    }

    public void setBuilder(OAuthBuilder builder) {
        this.builder = builder;
    }

    @JsonIgnore
    public Member getMember() {
        return Init.devarea.getMemberById(this.getAsSnowflake()).block();
    }

    public void setNext_xp_level(int next_xp_level) {
        this.next_xp_level = next_xp_level;
    }

    public int getNext_xp_level() {
        return next_xp_level;
    }

    public void setPrevious_xp_level(int previous_xp_level) {
        this.previous_xp_level = previous_xp_level;
    }

    public int getPrevious_xp_level() {
        return previous_xp_level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
