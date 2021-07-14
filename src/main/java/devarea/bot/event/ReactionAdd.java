package devarea.bot.event;

import devarea.bot.automatical.FreeLanceManager;
import devarea.bot.automatical.MeetupManager;
import devarea.bot.automatical.MissionsManager;
import devarea.bot.automatical.RolesReacts;
import devarea.bot.commands.CommandManager;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;

import static devarea.bot.data.TextMessage.messageDisableInPrivate;
import static devarea.bot.event.FunctionEvent.startAway;

public class ReactionAdd {

    public static void reactionAddFunction(ReactionAddEvent event) {
        try {
            if (!event.getMember().isPresent()) {
                final Message message = event.getMessage().block();
                message.getChannel().block().createMessage(messageCreateSpec -> messageCreateSpec.setContent(messageDisableInPrivate)).subscribe();
                return;
            }

            if (event.getMember().get().isBot())
                return;

            if (RolesReacts.onReact(event))
                return;


            startAway(() -> CommandManager.react(event));

            MeetupManager.getEvent(event);
            MissionsManager.react(event);
            FreeLanceManager.react(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
