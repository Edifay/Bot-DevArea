package devarea.event;

import devarea.Data.ColorsUsed;
import devarea.Main;
import devarea.automatical.Bump;
import devarea.automatical.MeetupManager;
import devarea.automatical.MissionsManager;
import devarea.automatical.Stats;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ready {

    private static boolean already = false;

    public static void readyEventFonction(final Snowflake idDevArea, final Snowflake idLogChannel) {

        Main.client.updatePresence(Presence.online(Activity.playing("//help | Dev'Area Server !"))).block();
        Main.devarea = Main.client.getGuildById(idDevArea).block();
        assert Main.devarea != null;
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (already)
            return;

        try {
            Stats.start();
            MeetupManager.init();
            Bump.init();
            MissionsManager.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Le bot est en ligne !");

        already = true;
    }
}
