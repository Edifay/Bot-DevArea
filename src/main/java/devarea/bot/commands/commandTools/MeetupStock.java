package devarea.bot.commands.commandTools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.global.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MeetupStock implements Serializable {

    @JsonProperty
    private String name;
    @JsonIgnore
    private Snowflake author;
    @JsonProperty
    private String idAuthor;
    @JsonProperty
    private Date date;
    @JsonProperty
    private String attachment;
    @JsonProperty
    private Boolean alreadyMade = false;
    @JsonProperty
    private MessageSeria message;

    public MeetupStock() {
    }

    @JsonIgnore
    public String getName() {
        return this.name;
    }

    @JsonIgnore
    public Snowflake getAuthor() {
        if (this.author == null)
            this.author = Snowflake.of(this.idAuthor);
        return this.author;
    }

    @JsonIgnore
    public Date getDate() {
        return this.date;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public void setAuthor(Snowflake id) {
        this.author = id;
        this.idAuthor = this.author.asString();
    }

    @JsonIgnore
    public void setDate(Date date) {
        this.date = date;
    }

    @JsonIgnore
    public void setAttachment(String url) {
        this.attachment = url;
    }

    @JsonIgnore
    public String getAttachment() {
        return this.attachment;
    }

    @JsonIgnore
    public MessageCreateSpec.Builder getEmbed() {
        MessageCreateSpec.Builder msgBuilder = MessageCreateSpec.builder();
        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder();
        if (this.name != null)
            builder.title(this.name);
        if (this.author != null) {
            Member member = MemberCache.get(this.author.asString());
            builder.author(member.getDisplayName(), member.getAvatarUrl(), member.getAvatarUrl());
        }
        if (this.attachment != null) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(ImageIO.read(new URL(this.attachment)), "png", outputStream);
                msgBuilder.addFile("image.png", new ByteArrayInputStream(outputStream.toByteArray()));
                builder.image("attachment://image.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (this.date != null) {
            builder.description("Le meetup aura lieu le " +
                    new SimpleDateFormat("dd/MM/yyyy").format(this.date) + " à " +
                    new SimpleDateFormat("HH").format(this.date) + "h" + new SimpleDateFormat("mm").format(this.date) + ".");
        }
        builder.color(ColorsUsed.just);
        return msgBuilder.addEmbed(builder.build());
    }

    @JsonIgnore
    public EmbedCreateSpec getEmbedVerif() {
        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder();

        builder.author("Voici comment sera afficher le meetup ! Vous pouvez confirmer (yes) ou annuler (cancel) le meetup.",
                Init.client.getSelf().block().getAvatarUrl(), Init.client.getSelf().block().getAvatarUrl());
        if (this.name != null)
            builder.title(this.name);
        if (this.author != null) {
            Member member = MemberCache.get(this.author.asString());
            builder.footer(member.getDisplayName(), member.getAvatarUrl());
        }
        if (this.attachment != null)
            builder.image(this.attachment);
        if (this.date != null)
            builder.description("Le meetup aura lieu le " +
                    new SimpleDateFormat("dd/MM/yyyy").format(this.date) + " à " +
                    new SimpleDateFormat("HH").format(this.date) + "h" + new SimpleDateFormat("mm").format(this.date) + ".");

        builder.color(ColorsUsed.just);

        return builder.build();
    }

    @JsonIgnore
    public MeetupStock getForWrite() {
        this.idAuthor = this.author.asString();
        this.author = null;
        return this;
    }

    @JsonIgnore
    public Boolean getAlreadyMade() {
        return this.alreadyMade;
    }

    @JsonIgnore
    public void setAlreadyMade(final boolean bool) {
        this.alreadyMade = bool;
    }

    @JsonIgnore
    public void setMessage(MessageSeria message) {
        this.message = message;
    }

    @JsonIgnore
    public MessageSeria getMessage() {
        return message;
    }
}
