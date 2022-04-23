package devarea.backend.controllers.tools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.backend.controllers.tools.badges.Badges;
import devarea.backend.controllers.rest.requestContent.RequestHandlerMission;
import devarea.bot.cache.MemberCache;
import devarea.bot.automatical.MissionsHandler;
import devarea.bot.automatical.XPHandler;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebUserInfo {

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
    protected WebMission[] missions_list;
    @JsonProperty
    protected String tag;
    @JsonProperty
    Badges[] badges;

    public WebUserInfo() {
    }

    public WebUserInfo(String id) {
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
    public WebUserInfo update() {
        Member member = MemberCache.get(this.id);

        // Simple Member Data
        this.id = member.getId().asString();
        this.name = member.getUsername();
        this.tag = member.getTag();
        this.urlAvatar = member.getAvatarUrl();
        this.badges = Badges.getBadgesOf(this, member).toArray(new Badges[0]);

        // Member Missions
        this.missions_list =
                RequestHandlerMission.transformMissionListToMissionWebList(MissionsHandler.getOf(Snowflake.of(this.id))).toArray(new WebMission[0]);

        // Member XP
        if (XPHandler.haveBeenSet(getAsSnowflake())) {
            this.rank = XPHandler.getRankOf(getAsSnowflake());
            this.setXp(XPHandler.getXpOf(getAsSnowflake()));
        } else {
            this.rank = XPHandler.getRankOf(getAsSnowflake());
            this.setXp(0);
        }

        return this;
    }

    @JsonIgnore
    public Member getMember() {
        return MemberCache.get(this.id);
    }

}
