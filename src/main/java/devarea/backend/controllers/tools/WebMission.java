package devarea.backend.controllers.tools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.bot.cache.MemberCache;
import devarea.bot.commands.commandTools.Mission;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;

import java.util.Objects;

public class WebMission {

    @JsonProperty("title")
    protected String title;
    @JsonProperty("description")
    protected String description;
    @JsonProperty("budget")
    protected String budget;
    @JsonProperty("deadLine")
    protected String deadLine;
    @JsonProperty("language")
    protected String language;
    @JsonProperty("support")
    protected String support;
    @JsonProperty("level")
    protected String level;
    @JsonProperty("memberName")
    protected String memberName;
    @JsonProperty("avatarURL")
    protected String avatarURL;
    @JsonProperty("memberTag")
    protected String memberTag;
    @JsonProperty("lastUpdate")
    protected String lastUpdate;
    @JsonProperty("id")
    protected String id;
    @JsonProperty("createdAt")
    protected long createdAt;
    @JsonProperty("memberID")
    protected String memberId;

    public WebMission(Mission mission_base) {
        this.mission = mission_base;

        this.title = mission_base.getTitle();
        this.description = mission_base.getDescriptionText();
        this.budget = mission_base.getBudget();
        this.deadLine = mission_base.getDeadLine();
        this.language = mission_base.getLanguage();
        this.support = mission_base.getSupport();
        this.level = mission_base.getNiveau();
        this.memberId = mission_base.getMemberId();
        this.id = mission_base.getId();
        this.createdAt = mission_base.getCreatedAt();

        Member member = MemberCache.get(mission_base.getMemberId());

        this.memberName = member.getDisplayName();
        this.avatarURL = member.getAvatarUrl();
        this.memberTag = member.getTag();

        this.lastUpdate = "" + ((System.currentTimeMillis() - mission_base.getLast_update()) / 86400000);
        if (this.lastUpdate.equals("0"))
            this.lastUpdate = "1";
    }

    public WebMissionPreview toPreview() {
        return new WebMissionPreview(this);
    }


    @JsonIgnore
    protected Mission mission;

    @JsonIgnore
    @Override
    public boolean equals(Object o) {
        if (o instanceof Snowflake)
            return this.mission.getMemberId().equals(((Snowflake) o).asString());
        else if (o instanceof String)
            return this.mission.getMemberId().equals(o);
        else if (o instanceof Mission)
            return this.mission.equals(o);

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebMission that = (WebMission) o;
        return Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(budget, that.budget) && Objects.equals(deadLine, that.deadLine) && Objects.equals(language, that.language) && Objects.equals(support, that.support) && Objects.equals(level, that.level) && Objects.equals(memberName, that.memberName) && Objects.equals(avatarURL, that.avatarURL) && Objects.equals(memberTag, that.memberTag) && Objects.equals(mission, that.mission);
    }

    public static class WebMissionPreview {

        @JsonProperty("title")
        protected String title;
        @JsonProperty("id")
        protected String id;
        @JsonProperty("lastUpdate")
        protected String lastUpdate;
        @JsonProperty("description")
        protected String description;
        @JsonProperty("avatarURL")
        protected String avatarURL;
        @JsonProperty("budget")
        protected String budget;

        public WebMissionPreview() {
        }

        public WebMissionPreview(final WebMission mission) {
            this.title = mission.title;
            this.id = mission.id;
            this.lastUpdate = mission.lastUpdate;
            this.description = mission.description.length() > 91 ? mission.description.substring(0, 91) :
                    mission.description;
            this.avatarURL = mission.avatarURL;
            this.budget = mission.budget;
        }
    }

}
