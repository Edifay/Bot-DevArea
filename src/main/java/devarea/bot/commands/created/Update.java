package devarea.bot.commands.created;

import devarea.bot.automatical.Stats;
import devarea.bot.commands.ShortCommand;
import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;

public class Update extends ShortCommand {

    public Update(MessageCreateEvent message) {
        super(message);
        final long ms = System.currentTimeMillis();
        commandWithPerm(Permission.ADMINISTRATOR, () -> {
            Stats.update();
            sendEmbed(embed -> {
                embed.setTitle("Update !");
                embed.setDescription("Les stats ont été update en " + (System.currentTimeMillis() - ms) + "ms.");
                embed.setColor(ColorsUsed.just);
            }, false);
        });
        this.endCommand();
    }
}
