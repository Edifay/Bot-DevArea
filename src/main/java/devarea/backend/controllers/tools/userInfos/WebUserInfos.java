package devarea.backend.controllers.tools.userInfos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.backend.controllers.handlers.UserInfosHandlers;
import devarea.backend.controllers.rest.requestContent.RequestHandlerMission;
import devarea.backend.controllers.tools.WebMission;
import devarea.backend.controllers.tools.badges.Badges;
import devarea.bot.automatical.MissionsHandler;
import devarea.bot.automatical.XPHandler;
import devarea.bot.cache.MemberCache;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class WebUserInfos {

    @JsonProperty
    protected String id;
    @JsonProperty
    protected String urlAvatar;
    @JsonProperty
    protected String name;
    @JsonProperty
    protected int rank;
    @JsonProperty
    protected int xp;
    @JsonProperty
    protected int previous_xp_level;
    @JsonProperty
    protected int next_xp_level;
    @JsonProperty
    protected int level;
    @JsonProperty
    protected WebMission.WebMissionPreview[] missions_list;
    @JsonProperty
    protected String tag;
    @JsonProperty
    protected String memberDescription;
    @JsonProperty
    Badges[] badges;

    public WebUserInfos(String id) {
        this.id = id;
    }


    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonIgnore
    public void setXp(int xp) {
        this.xp = xp;
        this.level = XPHandler.getLevelForXp(this.xp);
        this.previous_xp_level = XPHandler.getAmountForLevel(this.level);
        this.next_xp_level = XPHandler.getAmountForLevel(this.level + 1);
    }


    @JsonIgnore
    public Snowflake getAsSnowflake() {
        return Snowflake.of(this.id);
    }

    @JsonIgnore
    public WebUserInfos update() {
        Member member = MemberCache.get(this.id);
        if (member == null)
            return null;

        // Simple Member Data
        this.id = member.getId().asString();
        this.name = member.getUsername();
        this.tag = member.getTag();
        this.urlAvatar = member.getAvatarUrl();
        this.badges = Badges.getBadgesOf(this, member).toArray(new Badges[0]);

        // Member Missions
        this.missions_list =
                RequestHandlerMission.transformMissionsToWebMissionsPreview(MissionsHandler.getOf(Snowflake.of(this.id))).toArray(new WebMission.WebMissionPreview[0]);

        // Member XP
        if (XPHandler.haveBeenSet(getAsSnowflake())) {
            this.rank = XPHandler.getRankOf(getAsSnowflake());
            this.setXp(XPHandler.getXpOf(getAsSnowflake()));
        } else {
            this.rank = XPHandler.getRankOf(getAsSnowflake());
            this.setXp(0);
        }

        // UserData
        memberDescription = UserInfosHandlers.get(this.id).userDescription;

        return this;
    }

    @JsonIgnore
    public Member getMember() {
        return MemberCache.get(this.id);
    }
}
