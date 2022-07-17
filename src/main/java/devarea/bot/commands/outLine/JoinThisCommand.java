package devarea.bot.commands.outLine;

import devarea.bot.commands.SlashCommand;
import devarea.global.cache.MemberCache;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class JoinThisCommand extends ShortCommand implements PermissionCommand, SlashCommand {

    public JoinThisCommand(PermissionCommand p) {
        super();
    }

    public JoinThisCommand(Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        member = MemberCache.get(chatInteraction.getInteraction().getMember().get().getId().asString());
        if (chatInteraction.getOption("mention").isPresent() && chatInteraction.getOption("mention").get().getValue().isPresent()) {
            Snowflake userId = chatInteraction.getOption("mention").get().getValue().get().asSnowflake();
            CommandManager.logAs(member.getId(), userId);
            replyEmbed(EmbedCreateSpec.builder().title("Admin")
                    .description("Vous venez de vous log en tant que <@" + userId.asString() + ">")
                    .color(ColorsUsed.same).build(), false);
        } else
            replyError("Vous devez mention la personne à la quelle vous voulez vous log.");

    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.MANAGE_MESSAGES);
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return null;
    }
}
