package devarea.commands.created;

import devarea.Main;
import devarea.Data.ColorsUsed;
import devarea.automatical.MissionsManager;
import devarea.commands.ShortCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;

import java.util.Set;

import static devarea.Data.TextMessage.stopCommand;

public class Stop extends ShortCommand {

    public Stop(final MessageCreateEvent message) {
        super(message);
        this.commandWithPerm(Permission.ADMINISTRATOR, () -> {
            MissionsManager.stop();
            sendEmbed(embedCreateSpec -> {
                embedCreateSpec.setTitle(stopCommand);
                embedCreateSpec.setColor(ColorsUsed.wrong);
            });
            Main.client.logout().block();
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            for(Thread t : threadSet){
                t.interrupt();
            }
        });
        this.endCommand();
    }
}
