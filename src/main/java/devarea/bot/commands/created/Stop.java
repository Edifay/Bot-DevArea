package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.automatical.FreeLanceManager;
import devarea.bot.automatical.MissionsManager;
import devarea.bot.automatical.XpCount;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.Set;

import static devarea.bot.data.TextMessage.stopCommand;

public class Stop extends ShortCommand implements PermissionCommand {

    public Stop(PermissionCommand permissionCommand) {
        super();
    }

    public Stop(final MessageCreateEvent message) {
        super(message);
        MissionsManager.stop();
        FreeLanceManager.stop();
        XpCount.stop();
        sendEmbed(EmbedCreateSpec.builder()
                .title(stopCommand)
                .color(ColorsUsed.wrong).build(), false);
        Init.client.logout().block();
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread t : threadSet)
            t.interrupt();
        this.endCommand();
        System.exit(0);
    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }
}
