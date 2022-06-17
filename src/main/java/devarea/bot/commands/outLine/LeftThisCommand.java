package devarea.bot.commands.outLine;

import devarea.global.cache.MemberCache;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.PermissionSet;

public class LeftThisCommand extends ShortCommand implements PermissionCommand {

    public LeftThisCommand(PermissionCommand comm) {
        super();
    }

    public LeftThisCommand(Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        member = MemberCache.get(message.getAuthor().get().getId().asString());
        if (CommandManager.getLoggedAs(member.getId()) != null) {
            sendEmbed(EmbedCreateSpec.builder()
                    .title("Admin")
                    .description("Vous venez de vous déconnecter du log de <@" + CommandManager.getLoggedAs(member.getId()).asString() + ">")
                    .color(ColorsUsed.same).build(), false);
            CommandManager.unLog(member.getId());
        } else {
            sendError("Vous n'êtes loggé à personne !");
        }
    }

    @Override
    public PermissionSet getPermissions() {
        return new JoinThisCommand(() -> null).getPermissions();
    }
}
