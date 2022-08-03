package devarea.bot.event;

import devarea.bot.Init;
import devarea.bot.automatical.BumpHandler;
import devarea.bot.automatical.EmbedLinkHandler;
import devarea.bot.automatical.ThreadCreator;
import devarea.bot.commands.CommandManager;
import devarea.global.cache.MemberCache;
import devarea.global.handlers.XPHandler;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.MessageCreateSpec;

import java.util.Locale;

import static devarea.bot.presets.TextMessage.messageDisableInPrivate;
import static devarea.global.utils.ThreadHandler.startAway;

public class MessageCreate {

    public static void messageCreateFunction(final MessageCreateEvent message) {
        try {
            if (message.getMessage().getAuthor().isEmpty())
                return;

            if (message.getMessage().getAuthor().get().getId().equals(Init.initial.disboard_bot)) {
                BumpHandler.checkBumpAvailable();
                return;
            }

            if (message.getMessage().getAuthor().get().isBot() || message.getMessage().getAuthor().get().getId().equals(Init.client.getSelfId()))
                return;

            if (!message.getMember().isPresent()) {
                message.getMessage().getChannel().block().createMessage(MessageCreateSpec.builder().content(messageDisableInPrivate).build()).subscribe();
                return;
            } else
                MemberCache.use(message.getMember().get());

            if (message.getMessage().getChannelId().equals(Init.initial.bump_channel)) {
                message.getMessage().delete().subscribe();
                return;
            }

            startAway(() -> XPHandler.addXpToMember(message.getMember().get()));
            if (!message.getMessage().getContent().toLowerCase(Locale.ROOT).startsWith("//admin") && CommandManager.receiveMessage(message))
                return;

            if (message.getMessage().getContent().startsWith(Init.initial.prefix))
                CommandManager.exe(message.getMessage().getContent().substring(Init.initial.prefix.length()).split(
                        "\\s", 2)[0], message, null);

            ThreadCreator.newMessage(message);
            EmbedLinkHandler.onReceive(message);
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

}
