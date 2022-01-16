package devarea.bot.commands.created;

import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.ConsumableCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Admin extends ShortCommand {

    static {
        commands = new HashMap<>();
        setupBindedCommands();
    }

    private static HashMap<String, ConsumableCommand> commands;

    public Admin(final MessageCreateEvent message) {
        super(message);
        if (message.getMessage().getContent().split(" ").length == 1) {
            Command.sendEmbed(this.channel, embed -> {
                embed.setTitle("Voici les commandes disponibles :");
                embed.setColor(ColorsUsed.same);
                AtomicReference<String> allCommands = new AtomicReference<>("");
                commands.forEach((str, com) -> allCommands.set(allCommands.get() + "- `" + str + "`\n"));
                embed.setDescription(allCommands.get());
            }, false);
        } else {
            String firstArg = message.getMessage().getContent().split(" ")[1];
            commands.forEach((str, com) -> {
                if (firstArg.equalsIgnoreCase(str)) {
                    com.setMessageEvent(message);
                    CommandManager.addManualCommand(message.getMember().get(), com);
                }
            });
        }
    }

    public static void setupBindedCommands() {
        commands.put("join", new ConsumableCommand(JoinThisCommand.class) {
            @Override
            protected Command command() {
                return new JoinThisCommand(this.messageEvent);
            }
        });
        commands.put("left", new ConsumableCommand(LeftThisCommand.class) {
            @Override
            protected Command command() {
                return new LeftThisCommand(this.messageEvent);
            }
        });
    }
}
