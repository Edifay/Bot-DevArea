package devarea.bot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.Main;
import devarea.bot.commands.CommandManager;
import devarea.bot.event.*;
import devarea.bot.utils.InitialData;
import devarea.bot.utils.SnowflakeModuleSerializer;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.*;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildEmoji;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.gateway.intent.IntentSet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

public class Init {
    public static final File initialDataFile = new File("configuration.json");

    public static InitialData initial;

    public static GatewayDiscordClient client;
    public static Guild devarea;
    public static GuildEmoji idYes, idNo;

    public static TextChannel logChannel;

    public static BufferedImage
            backgroundXp,
            admin_badge,
            fonda_badge,
            graphist_badge,
            helper_badge,
            modo_badge,
            winner_badge,
            junior_badge,
            precursor_badge,
            senior_badge,
            profile_back,
            server_logo;

    public static void initBot() {
        CommandManager.init();

        try {
            // Setup mapper
            System.out.println(Main.separator + "Loading config");

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(SnowflakeModuleSerializer.snowflakeModule);

            if (initialDataFile.exists())
                initial = mapper.readValue(initialDataFile, new TypeReference<>() {
                });
            else {
                initial = new InitialData();
                mapper.writeValue(initialDataFile, initial);
                System.err.println("Vous devez configurer le fichier configuration.json !");
                System.exit(0);
            }

        } catch (Exception e) {
            System.err.println("Une erreur c'est produite dans le chargement de configuration.json !");
            e.printStackTrace();
            System.exit(0);
        }

        System.out.println("configuration.json loaded !");

        try {
            assetsLoader();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Les fichiers assets n'ont pas pu être chargé !!");
        }

        try {
            final String token = new Scanner(new FileInputStream("./token.token")).nextLine();

            client = DiscordClient.create(token)
                    .gateway()
                    .setEnabledIntents(IntentSet.all())
                    .login()
                    .block();

            assert client != null;

            client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> Ready.readyEventFonction());
            client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(MessageCreate::messageCreateFunction);
            client.getEventDispatcher().on(MessageUpdateEvent.class).subscribe(MessageUpdate::messageUpdateFunction);
            client.getEventDispatcher().on(MessageDeleteEvent.class).subscribe(MessageDelete::messageDeleteFunction);
            client.getEventDispatcher().on(MemberJoinEvent.class).subscribe(MemberJoin::memberJoinFunction);
            client.getEventDispatcher().on(MemberLeaveEvent.class).subscribe(MemberLeave::memberLeaveFunction);
            client.getEventDispatcher().on(ReactionAddEvent.class).subscribe(ReactionAdd::reactionAddFunction);
            client.getEventDispatcher().on(ReactionRemoveEvent.class).subscribe(ReactionRemove::FunctionReactionRemoveEvent);
            client.getEventDispatcher().on(VoiceStateUpdateEvent.class).subscribe(VoiceStateUpdate::VoiceStateUpdateFucntion);
            client.getEventDispatcher().on(ButtonInteractionEvent.class).subscribe(ButtonInteract::ButtonInteractFunction);
        } catch (Exception e) {
            System.err.println("Le token n'a pas pu être chargé ! (Ou erreur de login à discord !)");
        }
    }

    private static void assetsLoader() throws IOException {
        System.out.println(Main.separator + "Loading assets...");
        long ms = System.currentTimeMillis();
        backgroundXp = loadImageInPot(initial.xp_background);
        admin_badge = loadImageInPot(initial.admin_badge);
        fonda_badge = loadImageInPot(initial.fonda_badge);
        graphist_badge = loadImageInPot(initial.graphist_badge);
        helper_badge = loadImageInPot(initial.helper_badge);
        modo_badge = loadImageInPot(initial.modo_badge);
        winner_badge = loadImageInPot(initial.winner_badge);
        junior_badge = loadImageInPot(initial.junior_badge);
        precursor_badge = loadImageInPot(initial.precursor_badge);
        senior_badge = loadImageInPot(initial.senior_badge);
        profile_back = loadImageInPot(initial.profile_background);
        server_logo = loadImageInPot(initial.server_logo);
        System.out.println("Assets took : " + (System.currentTimeMillis() - ms) + "ms to load !");
    }

    public static BufferedImage loadImageInPot(String path) throws IOException {
        return ImageIO.read(Init.class.getResource(path).openStream());
    }

}