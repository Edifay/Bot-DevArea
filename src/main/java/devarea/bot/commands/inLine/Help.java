package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.presets.TextMessage;
import devarea.bot.commands.ShortCommand;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Permission;

public class Help extends ShortCommand {

    public Help(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        this.sendEmbed(TextMessage.helpEmbed, false);
        if (this.member.getBasePermissions().block().contains(Permission.ADMINISTRATOR) || this.member.getRoleIds().contains(Init.initial.admin_role) || this.member.getRoleIds().contains(Init.initial.modo_role)) {
            this.sendEmbed(TextMessage.helpEmbedAdmin, false);
        }
        this.endCommand();
    }
}
