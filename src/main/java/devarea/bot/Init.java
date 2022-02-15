package devarea.bot;

import devarea.bot.commands.CommandManager;
import devarea.bot.event.*;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.*;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.GuildEmoji;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.gateway.intent.IntentSet;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Init {

    public static String prefix;
    public static Boolean vanish;
    public static GatewayDiscordClient client = null;
    public static TextChannel logChannel;
    public static Snowflake idCategoryJoin;
    public static Guild devarea;

    public static GuildEmoji idYes, idNo;

    public static Snowflake
            idMissionsPayantes,
            idMissionsGratuites,
            idPresentation,
            idRolesChannel,
            idRoleRulesAccepted,
            idWelcomChannel,
            idGeneralChannel,
            idModo,
            idAdmin,
            idNoMic,
            idMeetupVerif,
            idMeetupAnnonce,
            idBump,
            idPingMeetup,
            idCategoryGeneral,
            idCommands,
            idDevHelper,
            idMissionsCategory,
            idVoiceChannelHelp,
            idFreeLance;

    public static BufferedImage backgroundXp;
    public static Document document;

    public static final ArrayList<Snowflake> membersId = new ArrayList<>();


    public static void initBot() {
        CommandManager.init();
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();

            final File fileNextToJar = new File("./config.xml");

            if (!fileNextToJar.exists()) {
                final OutputStream out = new FileOutputStream(fileNextToJar);
                out.write(Init.class.getResource("/assets/config.xml").openStream().readAllBytes());
                out.close();
            }

            String token;
            Snowflake idDevArea;
            Snowflake idLogChannel;
            Snowflake idJoinLogChannel;

            while (true) {
                try {
                    document = builder.parse(fileNextToJar);

                    token = new Scanner(new FileInputStream("./token.token")).nextLine();
                    prefix = document.getElementsByTagName("prefix").item(0).getChildNodes().item(0).getNodeValue();
                    vanish = Boolean.parseBoolean(document.getElementsByTagName("vanish").item(0).getChildNodes().item(0).getNodeValue());
                    idDevArea = getValue("idDevArea");
                    idLogChannel = getValue("idLogChannel");
                    idJoinLogChannel = getValue("idLogJoinChannel");
                    idCategoryJoin = getValue("idJoinCategory");
                    idMissionsGratuites = getValue("missions-gratuites");
                    idMissionsPayantes = getValue("mission-payantes");
                    idPresentation = getValue("idPresentation");
                    idRolesChannel = getValue("idRolesChannel");
                    idRoleRulesAccepted = getValue("RulesAccepted");
                    idWelcomChannel = getValue("idWelcomChannel");
                    idGeneralChannel = getValue("idGeneral");
                    idModo = getValue("idModo");
                    idAdmin = getValue("idAdmin");
                    idNoMic = getValue("noMic");
                    idMeetupVerif = getValue("idMeetupVerif");
                    idMeetupAnnonce = getValue("idMeetupAnnonce");
                    idBump = getValue("idBump");
                    idPingMeetup = getValue("idPingMeetup");
                    idCategoryGeneral = getValue("idCategoryGeneral");
                    backgroundXp = ImageIO.read(Init.class.getResource("/assets/backgroundXp.jpg").openStream());
                    idCommands = getValue("idCommands");
                    idDevHelper = getValue("idDevHelper");
                    idMissionsCategory = getValue("idMissionsCategory");
                    idVoiceChannelHelp = getValue("idVoiceChannelHelp");
                    idFreeLance = getValue("idFreeLance");

                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    final OutputStream out = new FileOutputStream(fileNextToJar);
                    out.write(Init.class.getResource("/assets/config.xml").openStream().readAllBytes());
                    out.close();
                }
            }

            client = DiscordClient.create(token)
                    .gateway()
                    .setEnabledIntents(IntentSet.all())
                    .login()
                    .block();

            final Snowflake finalIdDevArea = idDevArea;
            final Snowflake finalIdLogChannel = idLogChannel;
            final Snowflake finalIdJoinLogChannel = idJoinLogChannel;
            assert client != null;

            client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> Ready.readyEventFonction(finalIdDevArea, finalIdLogChannel));
            client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(MessageCreate::messageCreateFunction);
            client.getEventDispatcher().on(MessageUpdateEvent.class).subscribe(MessageUpdate::messageUpdateFunction);
            client.getEventDispatcher().on(MessageDeleteEvent.class).subscribe(MessageDelete::messageDeleteFunction);
            client.getEventDispatcher().on(MemberJoinEvent.class).subscribe(memberJoinEvent -> MemberJoin.memberJoinFunction(finalIdDevArea, finalIdJoinLogChannel, memberJoinEvent));
            client.getEventDispatcher().on(MemberLeaveEvent.class).subscribe(memberLeaveEvent -> MemberLeave.memberLeaveFunction(finalIdDevArea, finalIdJoinLogChannel, memberLeaveEvent));
            client.getEventDispatcher().on(ReactionAddEvent.class).subscribe(ReactionAdd::reactionAddFunction);
            client.getEventDispatcher().on(ReactionRemoveEvent.class).subscribe(ReactionRemove::FunctionReactionRemoveEvent);
            client.getEventDispatcher().on(VoiceStateUpdateEvent.class).subscribe(VoiceStateUpdate::VoiceStateUpdateFucntion);

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Snowflake getValue(String caseName) {
        return Snowflake.of(document.getElementsByTagName(caseName).item(0).getChildNodes().item(0).getNodeValue());
    }

}