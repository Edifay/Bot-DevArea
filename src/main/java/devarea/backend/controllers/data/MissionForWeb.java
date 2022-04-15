package devarea.backend.controllers.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.bot.Init;
import devarea.bot.commands.object_for_stock.Mission;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;

import java.util.Objects;

public class MissionForWeb {

    @JsonProperty("title")
    protected String title;
    @JsonProperty("description")
    protected String description;
    @JsonProperty("prix")
    protected String prix;
    @JsonProperty("date_retour")
    protected String date_retour;
    @JsonProperty("langage")
    protected String langage;
    @JsonProperty("support")
    protected String support;
    @JsonProperty("niveau")
    protected String niveau;
    @JsonProperty("member_name")
    protected String member_name;
    @JsonProperty("avatar")
    protected String member_url;
    @JsonProperty("member_tag")
    protected String member_tag;
    @JsonProperty("message_id")
    protected String message_id;
    @JsonProperty("last_update")
    protected String last_update;
    @JsonIgnore
    protected Mission mission;
    @JsonIgnore
    protected long lastTimeFetched;

    public MissionForWeb(Mission mission_base) {
        this.mission = mission_base;

        this.title = mission_base.getTitle();
        this.description = mission_base.getDescriptionText();
        this.prix = mission_base.getPrix();
        this.date_retour = mission_base.getDateRetour();
        this.langage = mission_base.getLangage();
        this.support = mission_base.getSupport();
        this.niveau = mission_base.getNiveau();
        this.message_id = mission_base.getMessage().getMessageID().asString();

        Member member = Init.devarea.getMemberById(Snowflake.of(mission_base.getMemberId())).block();

        this.member_name = member.getDisplayName();
        this.member_url = member.getAvatarUrl();
        this.member_tag = member.getTag();

        this.last_update = "" + ((System.currentTimeMillis() - mission_base.getLast_update()) / 86400000);
        if (this.last_update.equals("0"))
            this.last_update = "1";

        this.lastTimeFetched = System.currentTimeMillis();
    }

    public boolean verifyFetch(boolean force) {
        if (System.currentTimeMillis() - this.lastTimeFetched > 300000 || force) {
            Member member = Init.devarea.getMemberById(Snowflake.of(this.mission.getMemberId())).block();

            this.member_name = member.getDisplayName();
            this.member_url = member.getAvatarUrl();
            this.member_tag = member.getTag();

            this.lastTimeFetched = System.currentTimeMillis();

            return true;
        } else
            return false;
    }

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
        MissionForWeb that = (MissionForWeb) o;
        return Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(prix, that.prix) && Objects.equals(date_retour, that.date_retour) && Objects.equals(langage, that.langage) && Objects.equals(support, that.support) && Objects.equals(niveau, that.niveau) && Objects.equals(member_name, that.member_name) && Objects.equals(member_url, that.member_url) && Objects.equals(member_tag, that.member_tag) && Objects.equals(mission, that.mission);
    }

}
