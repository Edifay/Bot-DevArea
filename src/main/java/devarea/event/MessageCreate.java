package devarea.event;

import devarea.data.ColorsUsed;
import devarea.Main;
import devarea.automatical.Bump;
import devarea.automatical.XpCount;
import devarea.commands.Command;
import devarea.commands.CommandManager;
import devarea.commands.ExternalLongCommand;
import devarea.commands.LongCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static devarea.data.TextMessage.messageDisableInPrivate;

public class MessageCreate {

    public static void messageCreateFunction(final MessageCreateEvent message) {
        try {
            if (message.getMessage().getAuthor().get().getId().equals(Snowflake.of("302050872383242240")))
                Bump.getDisboardMessage(message);

            if (message.getMessage().getAuthor().get().isBot() || message.getMessage().getAuthor().get().getId().equals(Main.client.getSelfId()))
                return;

            if (message.getGuild().block() == null) {
                message.getMessage().getChannel().block().createMessage(messageCreateSpec -> messageCreateSpec.setContent(messageDisableInPrivate)).block();
                return;
            }

            if (message.getMessage().getChannel().block().getId().equals(Main.idBump) && !message.getMessage().getAuthor().get().equals(Main.devarea.getMemberById(Snowflake.of("302050872383242240"))))
                Bump.messageInChannel(message);

            Main.logChannel.createMessage(msg -> msg.setEmbed(embed -> {
                final DateTimeFormatter hours = DateTimeFormatter.ofPattern("HH:mm");
                final DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                final LocalDateTime now = LocalDateTime.now();
                embed.setColor(ColorsUsed.same);
                embed.setTitle(message.getMember().get().getTag() + " a envoyé un message :");
                embed.setDescription(message.getMessage().getContent());
                embed.setFooter(date.format(now) + " at " + hours.format(now) + ".", message.getMessage().getAuthor().get().getAvatarUrl());
            })).block();

            XpCount.onMessage(message);
            synchronized (CommandManager.key) {
                final AtomicReference<Boolean> find = new AtomicReference<>(false);
                for (Map.Entry<Snowflake, Command> entry : CommandManager.actualCommands.entrySet()) {
                    final Snowflake id = entry.getKey();
                    final Command command = entry.getValue();
                    if (id.equals(message.getMember().get().getId()))
                        if (command instanceof LongCommand) {
                            ((LongCommand) command).nextStape(message);
                            find.set(true);
                        } else if (command instanceof ExternalLongCommand) {
                            ((ExternalLongCommand) command).nextStape(message);
                            find.set(true);
                        }
                }
                if (find.get())
                    return;
            }

            if (message.getMessage().getContent().startsWith(Main.prefix))
                CommandManager.exe(message.getMessage().getContent().substring(Main.prefix.length()).split(" ")[0], message);
        } catch (
                Exception e) {
            System.out.println("can't crash");
            e.printStackTrace();
        }
    }

}
