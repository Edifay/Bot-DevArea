package devarea.event;

import devarea.automatical.FreeLanceManager;
import devarea.automatical.MeetupManager;
import devarea.automatical.MissionsManager;
import devarea.automatical.RolesReacts;
import devarea.commands.Command;
import devarea.commands.CommandManager;
import devarea.commands.LongCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;

import java.util.Map;

import static devarea.data.TextMessage.messageDisableInPrivate;

public class ReactionAdd {

    public static void reactionAddFunction(ReactionAddEvent reactionAddEvent) {
        try {
            final Message message = reactionAddEvent.getMessage().block();
            if (!reactionAddEvent.getMember().isPresent()) {
                message.getChannel().block().createMessage(messageCreateSpec -> messageCreateSpec.setContent(messageDisableInPrivate)).subscribe();
                return;
            }

            if (reactionAddEvent.getMember().get().isBot())
                return;

            RolesReacts.onReact(reactionAddEvent);

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
            FreeLanceManager.react(reactionAddEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
