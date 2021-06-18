package devarea.commands.created;

import devarea.automatical.MissionsManager;
import devarea.commands.ShortCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;

public class MissionUpdate extends ShortCommand {
    public MissionUpdate(MessageCreateEvent message) {
        super(message);
        this.commandWithPerm(Permission.ADMINISTRATOR, () -> {
            this.send(messageCreateSpec -> messageCreateSpec.setContent("Vous avez update le message des missions !"), false);
            MissionsManager.update();
        });
        this.endCommand();
    }
}
