package devarea.commands;

import devarea.Main;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageEditSpec;

import java.util.function.Consumer;

public abstract class Stape {

    public final static boolean end = true;
    public final static boolean next = false;

    protected final Stape[] stapes;
    protected Message message;
    protected Stape called;

    public Stape(Stape... stapes) {
        this.stapes = stapes;
    }

    protected abstract boolean onCall(Message message);

    protected boolean onReceiveMessage(MessageCreateEvent event) {
        sendErrorEntry();
        return next;
    }

    protected boolean onReceiveReact(ReactionAddEvent event) {
        sendErrorEntry();
        return next;
    }

    public boolean call(Message message) {
        this.message = message;
        return onCall(message);
    }

    public boolean receiveMessage(MessageCreateEvent event) {
        if (this.called == null)
            return onReceiveMessage(event);
        else
            return this.called.receiveMessage(event);

    }

    public boolean receiveReact(ReactionAddEvent event) {
        if (this.called == null)
            return onReceiveReact(event);
        else
            return this.called.receiveReact(event);
    }


    protected Stape stape(int nb) {
        return stapes[nb];
    }

    protected void setText(Consumer<? super EmbedCreateSpec> spec) {
        this.message.edit(msg -> msg.setEmbed(spec)).block();
    }

    protected void setMessage(Consumer<? super MessageEditSpec> spec) {
        this.message.edit(spec).block();
    }

    protected boolean call(Stape stape) {
        this.called = stape;
        return stape.call(this.message);
    }

    protected boolean callStape(int nb) {
        return this.call(stape(nb));
    }

    protected void sendErrorEntry() {
        Command.sendError((TextChannel) this.message.getChannel().block(), "Votre entrée n'est pas valide !");
    }

    protected void addYesEmoji() {
        this.message.addReaction(ReactionEmoji.custom(Main.devarea.getGuildEmojiById(Main.idYes).block())).subscribe();
    }

    protected void addNoEmoji() {
        this.message.addReaction(ReactionEmoji.custom(Main.devarea.getGuildEmojiById(Main.idNo).block())).subscribe();
    }

    protected void addYesNoEmoji() {
        addYesEmoji();
        addNoEmoji();
    }

    protected void removeAllEmoji() {
        this.message.removeAllReactions().subscribe();
    }

    protected boolean isYes(ReactionAddEvent event) {
        return ReactionEmoji.custom(Main.devarea.getGuildEmojiById(Main.idYes).block()).equals(event.getEmoji());
    }

    protected boolean isNo(ReactionAddEvent event) {
        return ReactionEmoji.custom(Main.devarea.getGuildEmojiById(Main.idNo).block()).equals(event.getEmoji());
    }

}
