package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.automatical.FreeLanceManager;
import devarea.bot.automatical.MissionsManager;
import devarea.bot.automatical.XpCount;
import devarea.bot.commands.ShortCommand;
import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;

import java.util.Set;

import static devarea.bot.data.TextMessage.stopCommand;

public class Stop extends ShortCommand {

    public Stop(final MessageCreateEvent message) {
        super(message);
        this.commandWithPerm(Permission.ADMINISTRATOR, () -> {
            MissionsManager.stop();
            FreeLanceManager.stop();
            XpCount.stop();
            sendEmbed(embedCreateSpec -> {
                embedCreateSpec.setTitle(stopCommand);
                embedCreateSpec.setColor(ColorsUsed.wrong);
            }, false);
            Init.client.logout().block();
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            for (Thread t : threadSet) {
                t.interrupt();
            }
            System.exit(0);
        });
        this.endCommand();
    }
}
