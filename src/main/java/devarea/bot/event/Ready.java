package devarea.bot.event;

import devarea.bot.data.ColorsUsed;
import devarea.bot.Init;
import devarea.bot.automatical.*;
import devarea.bot.github.GithubEvent;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static devarea.Main.developing;

public class Ready {

    private static boolean already = false;

    public static void readyEventFonction(final Snowflake idDevArea, final Snowflake idLogChannel) {

        Init.client.updatePresence(Presence.online(Activity.playing("//help | Dev'Area Server !"))).block();
        Init.devarea = Init.client.getGuildById(idDevArea).block();
        assert Init.devarea != null;
        Init.logChannel = (TextChannel) Init.devarea.getChannelById(idLogChannel).block();

        Init.logChannel.createMessage(msg -> msg.setEmbed(embed -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy");
            LocalDateTime now = LocalDateTime.now();
            embed.setColor(ColorsUsed.same);
            embed.setTitle("Bot Online !");
            embed.setDescription("Le bot a été allumé le " + dtf.format(now) + ".");
        })).subscribe();

        try {
            Stats.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (already)
            return;

        Init.idYes = Init.devarea.getGuildEmojiById(Snowflake.of(Init.document.getElementsByTagName("yes").item(0).getChildNodes().item(0).getNodeValue())).block();
        Init.idNo = Init.devarea.getGuildEmojiById(Snowflake.of(Init.document.getElementsByTagName("no").item(0).getChildNodes().item(0).getNodeValue())).block();

        try {
            RolesReacts.load();
            Stats.start();
            MeetupManager.init();
            XpCount.init();
            if(!developing) {
                Bump.init();
                MissionsManager.init();
                FreeLanceManager.init();
            }
            GithubEvent.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Le bot est en ligne !");

        already = true;
    }
}
