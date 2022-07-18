package devarea.bot.commands.commandTools;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.backend.controllers.tools.WebMission;
import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.MemberCache;
import devarea.global.handlers.MissionsHandler;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;

import java.time.Instant;
import java.util.Objects;

public class Mission {

    @JsonProperty("title")
    protected String title;
    @JsonProperty("description")
    protected String descriptionText;
    @JsonProperty("prix")
    protected String budget;
    @JsonProperty("dateRetour")
    protected String deadLine;
    @JsonProperty("langage")
    protected String language;
    @JsonProperty("support")
    protected String support;
    @JsonProperty("niveau")
    protected String niveau;
    @JsonProperty("id")
    protected String id;

    @JsonProperty("message")
    protected MessageSeria message;
    @JsonProperty("createdAt")
    protected long createdAt;
    @JsonProperty("membre")
    protected String memberId;
    @JsonProperty("last_update")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected long last_update;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("message_verification")
    protected MessageSeria message_verification;

    public Mission() {
        this.last_update = System.currentTimeMillis();
        this.createdAt = System.currentTimeMillis();
        this.id = MissionsHandler.generateID();
    }

    public Mission(final String title, final String descriptionText, final String prix, final String dateRetour,
                   final String langage, final String support, final String niveau, final String memberId,
                   final MessageSeria message) {
        this.title = title;
        this.descriptionText = descriptionText;
        this.budget = prix;
        this.deadLine = dateRetour;
        this.language = langage;
        this.support = support;
        this.niveau = niveau;
        this.message = message;
        this.memberId = memberId;
        this.id = MissionsHandler.generateID();
        this.last_update = System.currentTimeMillis();
        this.createdAt = System.currentTimeMillis();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public String getTitle() {
        return this.title;
    }

    public void setDescriptionText(String description) {
        this.descriptionText = description;
    }

    @JsonIgnore
    public String getDescriptionText() {
        return this.descriptionText;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    @JsonIgnore
    public String getBudget() {
        return this.budget;
    }

    public void setDeadLine(String date) {
        this.deadLine = date;
    }

    @JsonIgnore
    public String getDeadLine() {
        return this.deadLine;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonIgnore
    public String getLanguage() {
        return this.language;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    @JsonIgnore
    public String getSupport() {
        return this.support;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    @JsonIgnore
    public String getNiveau() {
        return this.niveau;
    }

    public void setMessage(MessageSeria message) {
        this.message = message;
    }

    @JsonIgnore
    public MessageSeria getMessage() {
        return this.message;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    @JsonIgnore
    public String getMemberId() {
        return this.memberId;
    }

    @JsonIgnore
    public long getLast_update() {
        return this.last_update;
    }

    public void setLast_update(long last_update) {
        this.last_update = last_update;
    }

    @JsonIgnore
    public MessageSeria getMessage_verification() {
        return message_verification;
    }

    @JsonIgnore
    public void setMessage_verification(MessageSeria message_verification) {
        this.message_verification = message_verification;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public void update() {
        this.last_update = System.currentTimeMillis();
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "Mission{" +
                "title='" + title + '\'' +
                ", descriptionText='" + descriptionText + '\'' +
                ", prix='" + budget + '\'' +
                ", dateRetour='" + deadLine + '\'' +
                ", langage='" + language + '\'' +
                ", support='" + support + '\'' +
                ", niveau='" + niveau + '\'' +
                ", message=" + message +
                ", memberId='" + memberId + '\'' +
                ", last_update=" + last_update +
                ", message_verification=" + message_verification +
                '}';
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public WebMission toWebMission(){
        return new WebMission(this);
    }

    @JsonIgnore
    @Override
    public boolean equals(Object o) {
        if (o instanceof WebMission) return o.equals(this);
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mission mission = (Mission) o;
        return Objects.equals(title, mission.title) && Objects.equals(descriptionText, mission.descriptionText) && Objects.equals(budget, mission.budget) && Objects.equals(deadLine, mission.deadLine) && Objects.equals(language, mission.language) && Objects.equals(support, mission.support) && Objects.equals(niveau, mission.niveau) && Objects.equals(message, mission.message) && Objects.equals(memberId, mission.memberId);
    }

    @JsonIgnore
    public EmbedCreateSpec getPrefabricatedEmbed(){
        Member member = MemberCache.get(this.memberId);
        return EmbedCreateSpec.builder()
                .title(title)
                .description(this.descriptionText + "\n\nPrix: " + this.budget + "\nDate de retour: " + this.deadLine +
                        "\nType de support: " + this.support + "\nLangage: " + this.language + "\nNiveau estimé:" +
                        " " + this.niveau + "\n\nCette mission est posté par : " + "<@" + this.memberId + ">.")
                .color(ColorsUsed.just)
                .author(member.getDisplayName(), member.getAvatarUrl(), member.getAvatarUrl())
                .timestamp(Instant.now())
                .build();
    }
}