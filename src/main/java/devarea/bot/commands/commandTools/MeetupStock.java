package devarea.bot.commands.commandTools;

import devarea.bot.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MeetupStock implements Serializable {

    private String name;
    private String description;
    private Snowflake author;
    private String idAuthor;
    private Date date;
    private String attachment;
    private Boolean alreayMake = false;

    public MeetupStock() {
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Snowflake getAuthor() {
        if (this.author == null)
            this.author = Snowflake.of(this.idAuthor);
        return this.author;
    }

    public Date getDate() {
        return this.date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthor(Snowflake id) {
        this.author = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAttachment(String url) {
        this.attachment = url;
    }

    public String getAttachment() {
        return this.attachment;
    }

    public EmbedCreateSpec getEmbed() {
        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder();
        if (this.name != null)
            builder.title(this.name);
        if (this.author != null) {
            Member member = MemberCache.get(this.author.asString());
            builder.author(member.getDisplayName(), member.getAvatarUrl(), member.getAvatarUrl());
        }
        if (this.attachment != null)
            builder.image(this.attachment);
        if (this.date != null) {
            builder.description("Le meetup aura lieu le " +
                    new SimpleDateFormat("dd/MM/yyyy").format(this.date) + " à " +
                    new SimpleDateFormat("HH").format(this.date) + "h" + new SimpleDateFormat("mm").format(this.date) + ".");
        }
        builder.color(ColorsUsed.just);
        return builder.build();
    }

    public EmbedCreateSpec getEmbedVerif() {
        EmbedCreateSpec.Builder builder = EmbedCreateSpec.builder();

        builder.author("Voici comment sera afficher le meetup ! Vous pouvez comfirmer yes ou annuler cancel le meetup.", Init.client.getSelf().block().getAvatarUrl(), Init.client.getSelf().block().getAvatarUrl());
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

    public MeetupStock getForWrite() {
        this.idAuthor = this.author.asString();
        this.author = null;
        return this;
    }

    public Boolean getAlreayMake() {
        return this.alreayMake;
    }

    public void setAlreadyMake(final boolean bool) {
        this.alreayMake = bool;
    }

}
