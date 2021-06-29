package devarea.commands.created;

import devarea.Main;
import devarea.automatical.FreeLanceManager;
import devarea.automatical.MissionsManager;
import devarea.commands.ShortCommand;
import devarea.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;

import java.util.Set;

import static devarea.data.TextMessage.stopCommand;

public class Stop extends ShortCommand {

    public Stop(final MessageCreateEvent message) {
        super(message);
        this.commandWithPerm(Permission.ADMINISTRATOR, () -> {
            MissionsManager.stop();
            FreeLanceManager.stop();
            sendEmbed(embedCreateSpec -> {
                embedCreateSpec.setTitle(stopCommand);
                embedCreateSpec.setColor(ColorsUsed.wrong);
            }, false);
            Main.client.logout().block();
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            for (Thread t : threadSet) {
                t.interrupt();
            }
        });
        this.endCommand();
    }
}
