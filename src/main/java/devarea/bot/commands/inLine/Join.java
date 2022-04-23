package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.commands.*;
import devarea.bot.commands.outLine.JoinCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class Join extends ShortCommand implements PermissionCommand {

    public Join(PermissionCommand permissionCommand) {
        super();
    }

    public Join(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        if (message.getUserMentions().size() > 0) {
            Member memberPinged = message.getUserMentions().get(0).asMember(Init.devarea.getId()).block();
            assert memberPinged != null;
            CommandManager.addManualCommand(memberPinged, new ConsumableCommand(JoinCommand.class) {
                @Override
                protected Command command() {
                    return new JoinCommand(this.member);
                }
            });
            sendEmbed(EmbedCreateSpec.builder()
                    .title("Vous avez fait join " + memberPinged.getDisplayName() + " !")
                    .color(ColorsUsed.just).build(), false);
        }
    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }
}
