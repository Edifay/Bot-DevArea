package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.automatical.XPHandler;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.Set;

import static devarea.bot.presets.TextMessage.stopCommand;

public class Stop extends ShortCommand implements PermissionCommand {

    public Stop(PermissionCommand permissionCommand) {
        super();
    }

    public Stop(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        XPHandler.stop();
        sendEmbed(EmbedCreateSpec.builder()
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
}
