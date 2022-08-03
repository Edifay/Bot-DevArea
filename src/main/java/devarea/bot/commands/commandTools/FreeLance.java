package devarea.bot.commands.commandTools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.Main;
import devarea.backend.controllers.tools.WebFreelance;
import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import devarea.global.handlers.FreeLanceHandler;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.discordjson.possible.Possible;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class FreeLance implements Comparable {

    @JsonProperty("name")
    protected String freeLanceName;
    @JsonProperty("description")
    protected String description;
    @JsonProperty("fields")
    protected ArrayList<FieldSeria> fields;

    @JsonProperty("message")
    protected MessageSeria message;
    @JsonProperty("membre")
    protected String memberId;
    @JsonProperty("last_bump")
    protected long last_bump;

    public FreeLance() {
        this.fields = new ArrayList<>();
        this.last_bump = System.currentTimeMillis();
    }

    public FreeLance(final MessageSeria message, final String memberId, final String freeLanceName) {
        this();
        this.message = message;
        this.memberId = memberId;
        this.freeLanceName = freeLanceName;
        this.last_bump = System.currentTimeMillis();
    }

    public FreeLance(WebFreelance web) {
        this.freeLanceName = web.name;
        this.description = web.description;
        this.fields = web.fields;
        this.memberId = web.member_id;

        this.last_bump = System.currentTimeMillis();
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
    public String getFreeLanceName() {
        return this.freeLanceName;
    }

    public void setFreeLanceName(String name) {
        this.freeLanceName = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public String getDescription() {
        return this.description;
    }

    public void addField(FieldSeria field) {
        this.fields.add(field);
    }

    @JsonIgnore
    public int getFieldNumber() {
        return this.fields.size();
    }

    @JsonIgnore
    public FieldSeria getField(int number) {
        return this.fields.get(number);
    }

    @JsonIgnore
    public EmbedCreateSpec getEmbed() {
        Member member = MemberCache.get(this.memberId);
        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder()
                .author(this.freeLanceName, null, member.getAvatarUrl())
                .description(this.getDescription())
                .color(ColorsUsed.same);
        for (int i = 0; i < this.getFieldNumber(); i++)
            builder.addField(this.getField(i).getTitle(), this.getField(i).getValue(), this.getField(i).getInline());
        builder.addField("Contact", "Pour contacter le freelancer voici son tag : " + member.getTag() + ", utilisez " +
                "directement sa mention : <@" + this.memberId + ">", false);
        return builder.build();
    }

    @Override
    public int compareTo(Object o) {

        FreeLance compareToEmp = (FreeLance) o;

        if (compareToEmp.last_bump == this.last_bump) return 0;

        if (this.last_bump < compareToEmp.last_bump) return 1;

        return -1;
    }

    public static class FieldSeria {

        @JsonProperty("title")
        protected String title;
        @JsonProperty("description")
        protected String description;
        @JsonProperty("prix")
        protected String prix;
        @JsonProperty("temps")
        protected String temps;
        @JsonProperty("inline")
        protected boolean inline;

        public FieldSeria() {
            this.title = "Empty";
            this.description = "Empty";
            this.prix = "Empty";
            this.temps = "Empty";
            this.inline = false;
        }

        public FieldSeria(final String title, final String description, final boolean inline) {
            this.title = title;
            this.description = description;
            this.inline = inline;
        }

        @JsonIgnore
        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @JsonIgnore
        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @JsonIgnore
        public boolean getInline() {
            return this.inline;
        }

        public void setInline(boolean inline) {
            this.inline = inline;
        }

        public void setPrix(String prix) {
            this.prix = prix;
        }

        @JsonIgnore
        public String getPrix() {
            return this.prix;
        }

        public void setTemps(String temps) {
            this.temps = temps;
        }

        @JsonIgnore
        public String getTemps() {
            return this.temps;
        }

        @JsonIgnore
        public String getValue() {
            return this.description +
                    ((!this.prix.equalsIgnoreCase("empty") || !this.temps.equalsIgnoreCase("empty")) ? "\n" : "")
                    + (this.prix.equalsIgnoreCase("empty") ? "" : ("\nPrix: " + this.prix))
                    + (this.temps.equalsIgnoreCase("empty") ? "" : ("\nTemps de retour: " + this.temps));
        }

    }

    public ArrayList<FieldSeria> getFields() {
        return fields;
    }

    public void setLast_bump(long last_bump) {
        this.last_bump = last_bump;
    }

    public long getLast_bump() {
        return last_bump;
    }

    public void send() {
        this.setMessage(new MessageSeria(Objects.requireNonNull(Command.send((TextChannel) ChannelCache.watch(Init.initial.freelance_channel.asString()), MessageCreateSpec.builder()
                .content("**Freelance de <@" + this.memberId + "> :**")
                .addEmbed(this.getEmbed())
                .addComponent(ActionRow.of(Button.link(Main.domainName + "member-profile?member_id=" + this.memberId + "&open=1",
                        "devarea.fr")))
                .build(), true))));
        FreeLanceHandler.updateBottomMessage();
    }

    public void edit() {
        try {
            Message message = this.message.getMessage();
            message.edit(MessageEditSpec.builder()
                    .content(Possible.of(Optional.of("**Freelance de <@" + this.memberId + "> :**")))
                    .addEmbed(this.getEmbed())
                    .addComponent(ActionRow.of(Button.link(Main.domainName + "member-profile?member_id=" + this.memberId + "&open=1",
                            "devarea.fr")))
                    .build()).block();
        } catch (Exception e) {
            this.send();
        }
    }

    public void delete() {
        try {
            Command.delete(false, message.getMessage());
        } catch (Exception ignored) {
        }
    }
}
