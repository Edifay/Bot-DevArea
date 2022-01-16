package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.data.TextMessage;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class Send extends ShortCommand implements PermissionCommand {

    public Send(PermissionCommand permissionCommand) {
        super();
    }

    public Send(final MessageCreateEvent message) {
        super(message);
        final String strMessage = message.getMessage().getContent().substring(Init.prefix.length() + "ping".length());
        if (!strMessage.isEmpty())
            send(msg -> {
                msg.setContent(strMessage);
                if (!message.getMember().get().getBasePermissions().block().contains(Permission.ADMINISTRATOR))
                    msg.setAllowedMentions(null);
            }, false);
        else
            sendError(TextMessage.errorNeedArguments);
        this.endCommand();
    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.MANAGE_MESSAGES);
    }
}
