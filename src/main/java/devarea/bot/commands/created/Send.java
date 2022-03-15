package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.data.TextMessage;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class Send extends ShortCommand implements PermissionCommand {

    public Send(PermissionCommand permissionCommand) {
        super();
    }

    public Send(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        final String strMessage = message.getContent().substring(Init.prefix.length() + "ping".length());
        if (!strMessage.isEmpty())
            send(MessageCreateSpec.builder().content(strMessage).build(), false);
        else
            sendError(TextMessage.errorNeedArguments);
        this.endCommand();
    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.MANAGE_MESSAGES);
    }
}
