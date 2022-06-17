package devarea.bot.event;

import devarea.global.cache.MemberCache;
import devarea.bot.automatical.*;
import devarea.bot.commands.CommandManager;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;

import static devarea.bot.presets.TextMessage.messageDisableInPrivate;

public class ReactionAdd {

    public static void reactionAddFunction(ReactionAddEvent event) {
        try {
            if (event.getMember().isEmpty()) {
                final Message message = event.getMessage().block();
                message.getChannel().block().createMessage(MessageCreateSpec.builder().content(messageDisableInPrivate).build()).subscribe();
                return;
            } else
                MemberCache.use(event.getMember().get());

            if (event.getMember().get().isBot() || RolesReactsHandler.onReact(event))
                return;

            if (CommandManager.react(event) || MeetupHandler.getEvent(event) || HelpRewardHandler.react(event))
                return;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
