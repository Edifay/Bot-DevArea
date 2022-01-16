package devarea.bot.commands.created;

import devarea.bot.automatical.MissionsManager;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class MissionUpdate extends ShortCommand implements PermissionCommand {

    public MissionUpdate(PermissionCommand permissionCommand) {
        super();
    }

    public MissionUpdate(MessageCreateEvent message) {
        super(message);
        this.send(messageCreateSpec -> messageCreateSpec.setContent("Vous avez update le message des missions !"), false);
        MissionsManager.update();
        this.endCommand();
    }


    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }
}
