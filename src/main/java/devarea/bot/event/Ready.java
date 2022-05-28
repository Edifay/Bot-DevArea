package devarea.bot.event;

import devarea.backend.controllers.handlers.UserInfosHandlers;
import devarea.backend.controllers.rest.requestContent.RequestHandlerAuth;
import devarea.bot.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.automatical.*;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.presence.*;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static devarea.Main.developing;
import static devarea.bot.event.FunctionEvent.startAway;

public class Ready {

    private static boolean already = false;

    public static void readyEventFonction(final Snowflake idDevArea, final Snowflake idLogChannel) {
        Init.client.updatePresence(ClientPresence.of(Status.ONLINE, ClientActivity.playing("//help | Dev'Area Server " +
                "!"))).subscribe();
        Init.devarea = Init.client.getGuildById(idDevArea).block();
        assert Init.devarea != null;
        startAway(() -> {
            Init.logChannel = (TextChannel) Init.devarea.getChannelById(idLogChannel).block();

            Button button = Button.primary("id_1", "Le button");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy");
            LocalDateTime now = LocalDateTime.now();
            Init.logChannel.createMessage(MessageCreateSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder().color(ColorsUsed.same)
                            .title("Bot Online !")
                            .description("Le bot a été allumé le " + dtf.format(now) + ".")
                            .build())
                    .build()
            ).subscribe();
        });


        System.out.println("Fetching members...");
        long ms = System.currentTimeMillis();

        MemberCache.use(Init.devarea.getMembers().buffer().blockLast().toArray(new Member[0]));

        System.out.println("Fetch took : " + (System.currentTimeMillis() - ms) + "ms, " + MemberCache.cacheSize() +
                " members fetch !");
        if (MemberCache.cacheSize() == 0)
            System.exit(0);


        try {
            StatsHandler.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (already)
            return;

        Init.idYes =
                Init.devarea.getGuildEmojiById(Snowflake.of(Init.document.getElementsByTagName("yes").item(0).getChildNodes().item(0).getNodeValue())).block();
        startAway(() -> Init.idNo = Init.devarea.getGuildEmojiById(Snowflake.of(Init.document.getElementsByTagName(
                "no").item(0).getChildNodes().item(0).getNodeValue())).block());

        try {
            startAway(RolesReactsHandler::load);
            startAway(StatsHandler::start);
            startAway(MeetupHandler::init);
            startAway(XPHandler::init);
            if (!developing)
                startAway(Bump::init);
            startAway(MissionsHandler::init);
            startAway(FreeLanceHandler::init);
            startAway(UserInfosHandlers::init);
            startAway(RequestHandlerAuth::init);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Le bot est en ligne !");

        already = true;

        startAway(() -> {
            try {
                while (true) {
                    Thread.sleep(86400000);
                    MemberCache.use(Init.devarea.getMembers().buffer().blockLast().toArray(new Member[0]));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
