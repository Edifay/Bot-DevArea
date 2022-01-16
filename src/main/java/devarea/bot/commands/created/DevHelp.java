package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.commands.ShortCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;

import java.util.ArrayList;

public class DevHelp extends ShortCommand {

    private static ArrayList<Snowflake> timer = new ArrayList<>();

    public DevHelp(MessageCreateEvent message) {
        super(message);
        if (((TextChannel) message.getMessage().getChannel().block()).getName().equalsIgnoreCase("entraide")) {
            if (!timer.contains(this.channel.getId())) {
                send(messageCreateSpec -> messageCreateSpec.setContent("<@" + this.member.getId().asString() + ">, a demandé de l'aide ! <@&" + Init.idDevHelper.asString() + ">."), false);
                timer.add(this.channel.getId());
                new Thread(() -> {
                    try {
                        Thread.sleep(1800000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        timer.remove(channel.getId());
                    }
                }).start();
            } else
                sendError("La commande devhelp n'est disponible que toutes les 30 minutes.");
        } else
            sendError("Uniquement les channels d'entraide acceptent cette commande.");
        this.endCommand();
    }
}
