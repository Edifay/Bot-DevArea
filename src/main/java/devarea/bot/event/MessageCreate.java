package devarea.bot.event;

import devarea.bot.Init;
import devarea.bot.automatical.Bump;
import devarea.bot.automatical.XpCount;
import devarea.bot.commands.CommandManager;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static devarea.bot.data.TextMessage.messageDisableInPrivate;
import static devarea.bot.event.FunctionEvent.startAway;

public class MessageCreate {

    public static void messageCreateFunction(final MessageCreateEvent message) {
        try {
            if (message.getMessage().getAuthor().get().getId().equals(Snowflake.of("302050872383242240")))
                Bump.getDisboardMessage(message);

            if (message.getMessage().getAuthor().get().isBot() || message.getMessage().getAuthor().get().getId().equals(Init.client.getSelfId()))
                return;

            if (!message.getMember().isPresent()) {
                message.getMessage().getChannel().block().createMessage(messageCreateSpec -> messageCreateSpec.setContent(messageDisableInPrivate)).subscribe();
                return;
            }

            if (message.getMessage().getChannelId().equals(Init.idBump) && !message.getMessage().getAuthor().get().getId().equals(Snowflake.of("302050872383242240")))
                Bump.messageInChannel(message);

            Init.logChannel.createMessage(msg -> msg.setEmbed(embed -> {
                final DateTimeFormatter hours = DateTimeFormatter.ofPattern("HH:mm");
                final DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                final LocalDateTime now = LocalDateTime.now();
                embed.setColor(ColorsUsed.same);
                embed.setTitle(message.getMember().get().getTag() + " a envoyé un message :");
                embed.setDescription(message.getMessage().getContent());
                embed.setFooter(date.format(now) + " at " + hours.format(now) + ".", message.getMessage().getAuthor().get().getAvatarUrl());
            })).subscribe();

            startAway(() -> XpCount.onMessage(message));
            if (CommandManager.receiveMessage(message) && !message.getMessage().getContent().toLowerCase(Locale.ROOT).startsWith("//admin"))
                return;

            if (message.getMessage().getContent().startsWith(Init.prefix))
                CommandManager.exe(message.getMessage().getContent().substring(Init.prefix.length()).split(" ")[0], message);
        } catch (
                Exception e) {
            System.out.println("can't crash");
            e.printStackTrace();
        }
    }

}
