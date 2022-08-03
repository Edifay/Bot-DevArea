package devarea.bot.commands.outLine;

import devarea.bot.commands.CommandManager;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.MemberCache;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.PermissionSet;

public class LeftThisCommand extends ShortCommand implements PermissionCommand {

    public LeftThisCommand(PermissionCommand comm) {
        super();
    }

    public LeftThisCommand(Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        member = MemberCache.get(chatInteraction.getInteraction().getMember().get().getId().asString());
        if (CommandManager.getLoggedAs(member.getId()) != null) {
            replyEmbed(EmbedCreateSpec.builder()
                    .title("Admin")
                    .description("Vous venez de vous déconnecter du log de <@" + CommandManager.getLoggedAs(member.getId()).asString() + ">")
                    .color(ColorsUsed.same).build(), false);
            CommandManager.unLog(member.getId());
        } else
            replyError("Vous n'êtes loggé à personne !");
    }

    @Override
    public PermissionSet getPermissions() {
        return new JoinThisCommand(() -> null).getPermissions();
    }
}
