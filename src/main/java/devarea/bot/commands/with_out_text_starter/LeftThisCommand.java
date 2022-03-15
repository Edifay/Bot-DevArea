package devarea.bot.commands.with_out_text_starter;

import devarea.bot.Init;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.data.ColorsUsed;
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
        member = Init.devarea.getMemberById(message.getAuthor().get().getId()).block();
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
