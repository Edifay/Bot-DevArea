package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.data.TextMessage;
import devarea.bot.commands.ShortCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;

public class Help extends ShortCommand {

    public Help(final MessageCreateEvent message) {
        super(message);
        this.sendEmbed(TextMessage.helpEmbed, false);
        if (this.member.getBasePermissions().block().contains(Permission.ADMINISTRATOR) || this.member.getRoleIds().contains(Init.idAdmin) || this.member.getRoleIds().contains(Init.idModo)) {
            this.sendEmbed(TextMessage.helpEmbedAdmin, false);
        }
        this.endCommand();
    }
}
