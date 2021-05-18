package devarea.event;

import devarea.automatical.MeetupManager;
import devarea.automatical.MissionsManager;
import devarea.commands.Command;
import devarea.commands.CommandManager;
import devarea.commands.LongCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;

import java.util.Map;

import static devarea.Data.TextMessage.messageDisableInPrivate;
import static devarea.automatical.RolesReacts.onReact;

public class ReactionAdd {

    public static void reactionAddFunction(ReactionAddEvent reactionAddEvent) {
        final Message message = reactionAddEvent.getMessage().block();
        if (reactionAddEvent.getGuild().block() == null) {
            message.getChannel().block().createMessage(messageCreateSpec -> messageCreateSpec.setContent(messageDisableInPrivate)).block();
            return;
        }

        if (reactionAddEvent.getUser().block().isBot())
            return;

        onReact(reactionAddEvent);

        synchronized (CommandManager.key) {
            for (Map.Entry<Snowflake, Command> entry : CommandManager.actualCommands.entrySet()) {
                Snowflake key = entry.getKey();
                Command command = entry.getValue();
                if (key.equals(reactionAddEvent.getUserId()))
                    if (command instanceof LongCommand)
                        ((LongCommand) command).nextStape(reactionAddEvent);
            }
        }

        MemberJoin.bindJoin.forEach((id, joining) -> {
            if (id.equals(reactionAddEvent.getUserId()))
                joining.next(reactionAddEvent);
        });

        MeetupManager.getEvent(reactionAddEvent);
        MissionsManager.react(reactionAddEvent);
    }

}
