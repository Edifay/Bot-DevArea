package devarea.commands;

import devarea.Data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;

public abstract class LongCommand extends Command {

    protected Message lastMessage;
    protected FirstStape firstStape;

    public LongCommand(final MessageCreateEvent message) {
        super(message);
    }

    public void nextStape(final ReactionAddEvent event) {
        Message message = event.getMessage().block();
        if (!message.getId().equals(this.lastMessage.getId())) {
            deletedEmbed((TextChannel) message.getChannel().block(), embed -> {
                embed.setTitle("Error !");
                embed.setDescription("Vous avez une commande en cour dans <#" + this.channel.getId().asString() + ">");
                embed.setColor(ColorsUsed.wrong);
            });
            return;
        }
        if (this.firstStape.receiveReact(event)) {
            this.ended = true;
            this.endCommand();
        }
        message.removeReaction(event.getEmoji(), event.getUserId()).block();
    }

    public void nextStape(final MessageCreateEvent event) {
        if (!event.getMessage().getChannelId().equals(this.channel.getId())) {
            deletedEmbed((TextChannel) event.getMessage().getChannel().block(), embed -> {
                embed.setTitle("Error !");
                embed.setDescription("Vous avez une commande en cour dans <#" + this.channel.getId().asString() + ">");
                embed.setColor(ColorsUsed.wrong);
            });
            delete(event.getMessage());
            return;
        }
        if (event.getMessage().getContent().startsWith("cancel") || event.getMessage().getContent().startsWith("annuler"))
            this.removeTrace();
        else if (this.firstStape.receiveMessage(event)) {
            this.ended = true;
            this.endCommand();
        }
        delete(event.getMessage());
    }

    ;

    protected void removeTrace() {
        sendError("Vous avez annuler la commande !");
        delete(this.lastMessage);
        ended = true;
        endCommand();
    }
}