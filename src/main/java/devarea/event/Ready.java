package devarea.event;

import devarea.Main;
import devarea.Data.ColorsUsed;
import devarea.automatical.Bump;
import devarea.automatical.MeetupManager;
import devarea.automatical.Stats;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ready {
    public static void readyEventFonction(final Snowflake idDevArea, final Snowflake idLogChannel) {

        Main.client.updatePresence(Presence.online(Activity.playing("//help | Dev'Area Server !"))).block();

        Main.devarea = Main.client.getGuildById(idDevArea).block();
        Main.logChannel = (TextChannel) Main.devarea.getChannelById(idLogChannel).block();

        Main.logChannel.createMessage(msg -> msg.setEmbed(embed -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy");
            LocalDateTime now = LocalDateTime.now();
            embed.setColor(ColorsUsed.same);
            embed.setTitle("Bot Online !");
            embed.setDescription("Le bot a été allumé le " + dtf.format(now) + ".");
        })).block();

        try {
            Stats.init();
            Stats.start();
            MeetupManager.init();
            Bump.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Le bot est en ligne !");

    }
}
