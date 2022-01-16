package devarea.bot.commands.created;

import devarea.bot.commands.CommandManager;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class JoinThisCommand extends ShortCommand implements PermissionCommand {

    public JoinThisCommand(PermissionCommand p) {
        super();
    }

    public JoinThisCommand(final MessageCreateEvent event) {
        super(event);
        Snowflake userId = getMention(event);
        if (userId != null) {
            if (CommandManager.hasCommand(userId)) {
                if (!(CommandManager.getCommandOf(userId) instanceof PermissionCommand) || CommandManager.containPerm(((PermissionCommand) CommandManager.getCommandOf(userId)).getPermissions(), event.getMember().get().getBasePermissions().block())) {
                    CommandManager.bindMemberToMember(event.getMember().get().getId(), userId);
                    sendEmbed(embed -> {
                        embed.setTitle("Admin");
                        embed.setDescription("Vous venez de vous attacher à la commande de <@" + CommandManager.getCommandOf(event.getMember().get().getId()).getMember().getId().asString() + ">");
                        embed.setColor(ColorsUsed.same);
                    }, false);
                } else {
                    sendError("Vous n'avez pas la permissions de rejoindre cette commande !");
                }
            } else {
                sendError("Erreur ! Le membre choisis n'est pas dans une commande !");
            }
        } else {
            sendError("Erreur avec la mention !");
        }
    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.MANAGE_MESSAGES);
    }
}
