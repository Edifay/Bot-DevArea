package devarea.bot.commands.object_for_stock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.bot.Init;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;

import java.util.ArrayList;
import java.util.function.Consumer;

public class FreeLance {

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

    public FreeLance() {
        this.fields = new ArrayList<>();
    }

    public FreeLance(final MessageSeria message, final String memberId, final String freeLanceName) {
        this();
        this.message = message;
        this.memberId = memberId;
        this.freeLanceName = freeLanceName;
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
    public Consumer<? super EmbedCreateSpec> getEmbed() {
        return embed -> {
            embed.setTitle(this.getFreeLanceName());
            embed.setDescription(this.getDescription());
            for (int i = 0; i < this.getFieldNumber(); i++) {
                embed.addField(this.getField(i).getTitle(), this.getField(i).getValue(), this.getField(i).getInline());
            }
            embed.addField("Contact", "Pour contacter le freelancer voici son tag : " + Init.devarea.getMemberById(Snowflake.of(this.memberId)).block().getTag() + ", utilisez directement ça mention : <@" + this.memberId + ">", false);
            embed.setColor(ColorsUsed.same);
        };
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
            return this.description + ((!this.prix.equalsIgnoreCase("empty") || !this.temps.equalsIgnoreCase("empty")) ? "\n" : "") + (this.prix.equalsIgnoreCase("empty") ? "" : ("\nPrix: " + this.prix) + (this.temps.equalsIgnoreCase("empty") ? "" : ("\nTemps de retour: " + this.temps)));
        }

    }
}
