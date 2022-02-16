package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.commands.ShortCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class DevHelp extends ShortCommand {

    private static ArrayList<Snowflake> timer = new ArrayList<>();

    public DevHelp(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        if (channel.getName().equalsIgnoreCase("entraide")) {
            if (!timer.contains(this.channel.getId())) {
                send(MessageCreateSpec.builder().content("<@" + this.member.getId().asString() + ">, a demandé de l'aide ! <@&" + Init.idDevHelper.asString() + ">.").build(), false);
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
