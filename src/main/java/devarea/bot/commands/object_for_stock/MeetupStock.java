package devarea.bot.commands.object_for_stock;

import devarea.bot.Init;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

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

    public Consumer<? super EmbedCreateSpec> getEmbed() {
        return (Consumer<EmbedCreateSpec>) embedCreateSpec -> {
            if (this.name != null) {
                embedCreateSpec.setTitle(this.name);
            }
            if (this.author != null) {
                Member member = Init.devarea.getMemberById(this.author).block();
                embedCreateSpec.setAuthor(member.getDisplayName(), member.getAvatarUrl(), member.getAvatarUrl());
            }
            if (this.attachment != null) {
                embedCreateSpec.setImage(this.attachment);
            }
            if (this.date != null) {
                embedCreateSpec.setDescription("Le meetup aura lieu le " +
                        new SimpleDateFormat("dd/MM/yyyy").format(this.date) + " à " +
                        new SimpleDateFormat("HH").format(this.date) + "h" + new SimpleDateFormat("mm").format(this.date) + ".");
            }
            embedCreateSpec.setColor(ColorsUsed.just);
        };
    }

    public Consumer<? super EmbedCreateSpec> getEmbedVerif() {
        return (Consumer<EmbedCreateSpec>) embedCreateSpec -> {
            embedCreateSpec.setAuthor("Voici comment sera afficher le meetup ! Vous pouvez comfirmer yes ou annuler cancel le meetup.", Init.client.getSelf().block().getAvatarUrl(), Init.client.getSelf().block().getAvatarUrl());
            if (this.name != null) {
                embedCreateSpec.setTitle(this.name);
            }
            if (this.author != null) {
                Member member = Init.devarea.getMemberById(this.author).block();
                embedCreateSpec.setFooter(member.getDisplayName(), member.getAvatarUrl());
            }
            if (this.attachment != null) {
                embedCreateSpec.setImage(this.attachment);
            }
            if (this.date != null) {
                embedCreateSpec.setDescription("Le meetup aura lieu le " +
                        new SimpleDateFormat("dd/MM/yyyy").format(this.date) + " à " +
                        new SimpleDateFormat("HH").format(this.date) + "h" + new SimpleDateFormat("mm").format(this.date) + ".");
            }
            embedCreateSpec.setColor(ColorsUsed.just);
        };
    }

    public MeetupStock getForWrite() {
        this.idAuthor = this.author.asString();
        this.author = null;
        return this;
    }

    public Boolean getAlreayMake(){
        return this.alreayMake;
    }

    public void setAlreadyMake(final boolean bool){
        this.alreayMake = bool;
    }

}
