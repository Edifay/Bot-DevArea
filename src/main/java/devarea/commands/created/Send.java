package devarea.commands.created;

import devarea.Main;
import devarea.Data.TextMessage;
import devarea.commands.ShortCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;

public class Send extends ShortCommand {

    public Send(final MessageCreateEvent message) {
        super(message);
        this.commandWithPerm(Permission.MANAGE_MESSAGES, () -> {
            final String strMessage = message.getMessage().getContent().substring(Main.prefix.length() + "ping".length());
            if (!strMessage.isEmpty())
                send(msg -> {
                    msg.setContent(strMessage);
                    if (!message.getMember().get().getBasePermissions().block().contains(Permission.ADMINISTRATOR))
                        msg.setAllowedMentions(null);
                });
            else
                sendError(TextMessage.errorNeedArguments);
        });
        this.endCommand();
    }
}
