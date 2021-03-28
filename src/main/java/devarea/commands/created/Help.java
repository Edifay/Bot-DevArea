package devarea.commands.created;

import devarea.Main;
import devarea.Data.TextMessage;
import devarea.commands.ShortCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;

public class Help extends ShortCommand {

    public Help(final MessageCreateEvent message) {
        super(message);
        this.sendEmbed(TextMessage.helpEmbed);
        if (this.member.getBasePermissions().block().contains(Permission.ADMINISTRATOR) || this.member.getRoleIds().contains(Main.idAdmin) || this.member.getRoleIds().contains(Main.idModo)) {
            this.sendEmbed(TextMessage.helpEmbedAdmin);
        }
        this.endCommand();
    }
}
