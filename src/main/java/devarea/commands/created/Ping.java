package devarea.commands.created;

import devarea.data.ColorsUsed;
import devarea.commands.ShortCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class Ping extends ShortCommand {

    public Ping(final MessageCreateEvent message) {
        super(message);
        this.sendEmbed(embed -> {
            embed.setTitle("Pong !");
            embed.setDescription("La latence avec le bot est de `" + (System.currentTimeMillis() - message.getMessage().getTimestamp().toEpochMilli()) + "`ms.");
            embed.setColor(ColorsUsed.same);
        }, false);
        this.endCommand();
    }
}
