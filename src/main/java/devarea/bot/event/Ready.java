package devarea.bot.event;

import devarea.backend.controllers.rest.ControllerOAuth2;
import devarea.bot.Init;
import devarea.bot.automatical.*;
import devarea.bot.data.ColorsUsed;
import devarea.bot.github.GithubEvent;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.presence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static devarea.Main.developing;
import static devarea.bot.event.FunctionEvent.startAway;

public class Ready {

    private static boolean already = false;

    public static void readyEventFonction(final Snowflake idDevArea, final Snowflake idLogChannel) {
        Init.client.updatePresence(ClientPresence.of(Status.ONLINE, ClientActivity.playing("//help | Dev'Area Server !"))).subscribe();
        Init.devarea = Init.client.getGuildById(idDevArea).block();
        assert Init.devarea != null;
        startAway(() -> {
            Init.logChannel = (TextChannel) Init.devarea.getChannelById(idLogChannel).block();

            Init.logChannel.createMessage(msg -> msg.setEmbed(embed -> {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy");
                LocalDateTime now = LocalDateTime.now();
                embed.setColor(ColorsUsed.same);
                embed.setTitle("Bot Online !");
                embed.setDescription("Le bot a été allumé le " + dtf.format(now) + ".");
            })).subscribe();
        });

        System.out.println("Fetching members...");
        long ms = System.currentTimeMillis();
        Init.membersId.removeAll(Init.membersId);
        Init.devarea.getMembers().buffer().blockLast().forEach(member -> Init.membersId.add(member.getId()));
        System.out.println("Fetch took : " + (System.currentTimeMillis() - ms) + "ms, " + Init.membersId.size() + " members fetch !");
        if (Init.membersId.size() == 0)
            System.exit(0);

        try {
            Stats.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (already)
            return;

        Init.idYes = Init.devarea.getGuildEmojiById(Snowflake.of(Init.document.getElementsByTagName("yes").item(0).getChildNodes().item(0).getNodeValue())).block();
        startAway(() -> Init.idNo = Init.devarea.getGuildEmojiById(Snowflake.of(Init.document.getElementsByTagName("no").item(0).getChildNodes().item(0).getNodeValue())).block());

        try {
            startAway(RolesReacts::load);
            startAway(Stats::start);
            startAway(MeetupManager::init);
            startAway(XpCount::init);
            if (!developing)
                startAway(Bump::init);
            startAway(MissionsManager::init);
            startAway(FreeLanceManager::init);

            startAway(GithubEvent::init);
            startAway(ControllerOAuth2::init);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Le bot est en ligne !");

        already = true;
    }
}
