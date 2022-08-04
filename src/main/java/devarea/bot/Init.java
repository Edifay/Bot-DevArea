package devarea.bot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.Main;
import devarea.bot.event.*;
import devarea.bot.utils.InitialData;
import devarea.global.utils.SnowflakeModuleSerializer;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.*;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildEmoji;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.gateway.intent.IntentSet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static devarea.global.utils.ThreadHandler.startAway;

public class Init {
    public static final File initialDataFile = new File("configuration.json");

    public static InitialData initial;

    public static GatewayDiscordClient client;
    public static Guild devarea;
    public static GuildEmoji idYes, idNo, idLoading;

    public static TextChannel logChannel;

    /*
        Setup dynamic assets from initalData
     */
    public static HashMap<String, BufferedImage> badgesImages = new HashMap<>();
    /*
        Setup dynamic assets from initalData
     */
    public static HashMap<String, BufferedImage> assetsImages = new HashMap<>();

    /*
        Initialise bot part
     */
    public static void initBot() {
        setupInitialConfig();
        assetsLoader();
        connectDiscordClient();

        assert client != null;

        // Setup ready event
        client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> Ready.readyEventFonction());
    }

    /*
         Setup connection with discord API,
     */
    private static void connectDiscordClient() {
        try {
            long ms = System.currentTimeMillis();
            System.out.println(Main.separator + "Connecting to Discord API.");
            final String token = new Scanner(new FileInputStream("./token.token")).nextLine();

            client = DiscordClient.create(token)
                    .gateway()
                    .setEnabledIntents(IntentSet.all())
                    .login()
                    .block();

            System.out.println("Connection success !");
        } catch (Exception e) {
            System.err.println("Le token n'a pas pu être chargé ! (Ou erreur de login à discord !)");
        }
    }

    /*
        Load configuration and set up the var -> initial
     */
    private static void setupInitialConfig() {
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
            System.err.println("Une erreur s'est produite dans le chargement de configuration.json ! : \n" + e.getMessage());
            System.exit(0);
        }

        System.out.println("configuration.json loaded !");
    }

    /*
        Setup all events when all init are done.
     */
    public static void setupEventDispatcher() {
        client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(messageCreateEvent -> startAway(() -> MessageCreate.messageCreateFunction(messageCreateEvent)));
        client.getEventDispatcher().on(MessageUpdateEvent.class).subscribe(messageUpdateEvent -> startAway(() -> MessageUpdate.messageUpdateFunction(messageUpdateEvent)));
        client.getEventDispatcher().on(MessageDeleteEvent.class).subscribe(messageDeleteEvent -> startAway(() -> MessageDelete.messageDeleteFunction(messageDeleteEvent)));
        client.getEventDispatcher().on(MemberJoinEvent.class).subscribe(memberJoinEvent -> startAway(() -> MemberJoin.memberJoinFunction(memberJoinEvent)));
        client.getEventDispatcher().on(MemberLeaveEvent.class).subscribe(memberLeaveEvent -> startAway(() -> MemberLeave.memberLeaveFunction(memberLeaveEvent)));
        client.getEventDispatcher().on(ReactionAddEvent.class).subscribe(reactionAddEvent -> startAway(() -> ReactionAdd.reactionAddFunction(reactionAddEvent)));
        client.getEventDispatcher().on(ReactionRemoveEvent.class).subscribe(reactionRemoveEvent -> startAway(() -> ReactionRemove.FunctionReactionRemoveEvent(reactionRemoveEvent)));
        client.getEventDispatcher().on(VoiceStateUpdateEvent.class).subscribe(voiceStateUpdateEvent -> startAway(() -> VoiceStateUpdate.VoiceStateUpdateFunction(voiceStateUpdateEvent)));
        client.getEventDispatcher().on(ButtonInteractionEvent.class).subscribe(buttonInteractionEvent -> startAway(() -> ButtonInteract.ButtonInteractFunction(buttonInteractionEvent)));
        client.getEventDispatcher().on(SelectMenuInteractionEvent.class).subscribe(selectMenuInteractionEvent -> startAway(() -> SelectMenuInteraction.SelectMenuInteractionFunction(selectMenuInteractionEvent)));

    }

    /*
        Setup assets used by the bot from initial
     */
    private static void assetsLoader() {
        try {
            System.out.println(Main.separator + "Loading assets...");
            long ms = System.currentTimeMillis();

            for (Map.Entry<String, String> entry : initial.assetsImages.entrySet())
                assetsImages.put(entry.getKey(), loadImageInPot(entry.getValue()));

            for (Map.Entry<String, String> entry : initial.badgesImages.entrySet())
                badgesImages.put(entry.getKey(), loadImageInPot(entry.getValue()));

            System.out.println("Assets took : " + (System.currentTimeMillis() - ms) + "ms to load !");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Les fichiers assets n'ont pas pu être chargé !!");
        }
    }

    /*
        Load an Image in the jar pot
     */
    public static BufferedImage loadImageInPot(String path) throws IOException {
        return ImageIO.read(Init.class.getResource(path).openStream());
    }

}