package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.commands.SlashCommand;
import devarea.global.handlers.UserDataHandler;
import devarea.global.handlers.XPHandler;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.Set;

import static devarea.bot.presets.TextMessage.stopCommand;

public class Stop extends ShortCommand implements PermissionCommand, SlashCommand {

    public Stop(PermissionCommand permissionCommand) {
        super();
    }

    public Stop(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        XPHandler.stop();
        UserDataHandler.updated();
        replyEmbed(EmbedCreateSpec.builder()
                .title(stopCommand)
                .color(ColorsUsed.wrong).build(), false);
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread t : threadSet)
            if (!t.equals(Thread.currentThread()))
                t.interrupt();
        Init.client.logout().block();
        this.endCommand();
        System.exit(0);
    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }

    public Stop() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("stop")
                .description("Commande pour Edifay ;) !")
                .build();
    }
}
