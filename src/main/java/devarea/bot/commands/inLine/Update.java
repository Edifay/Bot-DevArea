package devarea.bot.commands.inLine;

import devarea.bot.commands.SlashCommand;
import devarea.global.handlers.StatsHandler;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class Update extends ShortCommand implements PermissionCommand, SlashCommand {

    public Update(PermissionCommand permissionCommand) {
        super();
    }

    public Update(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        final long ms = System.currentTimeMillis();
        StatsHandler.update();
        replyEmbed(EmbedCreateSpec.builder()
                .title("Update !")
                .description("Les stats ont été update en " + (System.currentTimeMillis() - ms) + "ms.")
                .color(ColorsUsed.just).build(), false);
    }


    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }

    public Update() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("update")
                .description("Force la mise à jour des channels Statistiques.")
                .build();
    }
}
