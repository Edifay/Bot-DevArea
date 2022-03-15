package devarea.bot.commands.with_out_text_starter;

import devarea.bot.Init;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import org.w3c.dom.Text;

public class JoinThisCommand extends ShortCommand implements PermissionCommand {

    public JoinThisCommand(PermissionCommand p) {
        super();
    }

    public JoinThisCommand(Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        member = Init.devarea.getMemberById(message.getAuthor().get().getId()).block();
        if (message.getUserMentionIds().toArray(new Snowflake[0]).length != 0) {
            Snowflake userId = message.getUserMentionIds().toArray(new Snowflake[0])[0];
            if (userId != null) {
                CommandManager.logAs(member.getId(), userId);
                sendEmbed(EmbedCreateSpec.builder().title("Admin")
                        .description("Vous venez de vous log en tant que <@" + userId.asString() + ">")
                        .color(ColorsUsed.same).build(), false);
            } else {
                sendError("Erreur avec la mention !");
            }
        } else {
            sendError("Vous devez mention la personne à la quelle vous voulez vous log.");
        }

    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.MANAGE_MESSAGES);
    }
}
