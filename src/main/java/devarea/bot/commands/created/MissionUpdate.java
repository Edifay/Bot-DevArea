package devarea.bot.commands.created;

import devarea.bot.automatical.MissionsManager;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class MissionUpdate extends ShortCommand implements PermissionCommand {

    public MissionUpdate(PermissionCommand permissionCommand) {
        super();
    }

    public MissionUpdate(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        this.send(MessageCreateSpec.builder().content("Vous avez update le message des missions !").build(), false);
        MissionsManager.update();
        this.endCommand();
    }


    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }
}
