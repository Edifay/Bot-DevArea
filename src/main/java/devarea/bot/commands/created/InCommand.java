package devarea.bot.commands.created;

import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.LongCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.time.Instant;
import java.util.Map;

public class InCommand extends ShortCommand {
    public InCommand(MessageCreateEvent message) {
        super(message);
        this.sendEmbed(embedCreateSpec -> {
            embedCreateSpec.setTitle("Voici toutes les personnes ayant des commandes actives.");
            String text = "";
            if (CommandManager.size() > 0) {
                text += "Il y a actuellement " + CommandManager.size() + " commandes en cour :\n";
                for (Map.Entry<Snowflake, LongCommand> entry : CommandManager.getMap().entrySet()) {
                    String[] names = entry.getValue().getClass().getName().split("\\.");
                    text += "<@" + entry.getKey().asString() + "> : " + names[names.length - 1] + "\n";
                }
            } else
                text = "Il n'y actuellement personnes avec des commandes en cour.";
            embedCreateSpec.setDescription(text);
            embedCreateSpec.setColor(ColorsUsed.same);
            embedCreateSpec.setTimestamp(Instant.now());
        }, false);
        this.endCommand();
    }
}
