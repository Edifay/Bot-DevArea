package devarea.bot.commands;

import devarea.bot.Init;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageEditSpec;

import java.util.function.Consumer;

public abstract class Stape implements Cloneable {

    public final static boolean end = true;
    public final static boolean next = false;

    protected Stape[] stapes;
    protected Message message;
    protected Stape called;

    public Stape(Stape... stapes) {
        assert stapes.length != 0;
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


    protected Stape stape(int nb) throws Exception {
        if (nb < 0 || nb >= stapes.length) {
            throw new Exception("Le numero de la stape n'est pas associe !");
        }
        return stapes[nb];
    }

    protected void setText(Consumer<? super EmbedCreateSpec> spec) {
        this.message.edit(msg -> msg.setEmbed(spec)).subscribe();
    }

    protected void setMessage(Consumer<? super MessageEditSpec> spec) {
        this.message.edit(spec).subscribe();
    }

    protected boolean call(Stape stape) {
        try {
            this.called = ((Stape) stape.clone());
            return this.called.call(this.message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return next;
    }

    protected boolean callStape(int nb) {
        try {
            return this.call(stape(nb));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return end;
    }

    protected void sendErrorEntry() {
        Command.sendError((TextChannel) this.message.getChannel().block(), "Votre entrée n'est pas valide !");
    }

    protected void addYesEmoji() {
        this.message.addReaction(ReactionEmoji.custom(Init.idYes)).subscribe();
    }

    protected void addNoEmoji() {
        this.message.addReaction(ReactionEmoji.custom(Init.idNo)).subscribe();
    }

    protected void addYesNoEmoji() {
        addYesEmoji();
        addNoEmoji();
    }

    protected void removeAllEmoji() {
        this.message.removeAllReactions().subscribe();
    }

    protected void removeNoEmoji() {
        this.message.removeReactions(ReactionEmoji.custom(Init.idNo)).subscribe();
    }

    protected void removeYesEmoji() {
        this.message.removeReactions(ReactionEmoji.custom(Init.idYes)).subscribe();
    }

    protected boolean isYes(ReactionAddEvent event) {
        return ReactionEmoji.custom(Init.idYes).equals(event.getEmoji());
    }

    protected boolean isNo(ReactionAddEvent event) {
        return ReactionEmoji.custom(Init.idNo).equals(event.getEmoji());
    }

    protected String getContent(MessageCreateEvent event) {
        return event.getMessage().getContent();
    }

}
