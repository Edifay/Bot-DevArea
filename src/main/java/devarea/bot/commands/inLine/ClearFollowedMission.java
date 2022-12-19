package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.commands.SlashCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.Category;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class ClearFollowedMission extends ShortCommand implements SlashCommand, PermissionCommand {

    public ClearFollowedMission() {
        super();
    }

    public ClearFollowedMission(final Member member, final ChatInputInteractionEvent chatInteraction) {
        chatInteraction.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Followed Mission Cleared.")
                        .color(ColorsUsed.same)
                        .description("Les suivis de missions fermé vont petit à petit être supprimé !")
                        .build())
                .build()).block();
        ((Category) Init.devarea.getChannelById(Snowflake.of("964757205184299028")).block()).getChannels().toIterable().forEach(categorizableChannel -> {
            if (categorizableChannel.getName().startsWith("closed")) {
                categorizableChannel.delete().block();
            }
        });
    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.MANAGE_CHANNELS);
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("clearfollowedmission")
                .description("Supprime tous les suivis de missions fermé !")
                .build();
    }
}
