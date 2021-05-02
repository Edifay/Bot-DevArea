package devarea;

import devarea.automatical.XpCount;
import devarea.commands.CommandManager;
import devarea.event.*;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

public class Main {

    public static String prefix;
    public static Boolean vanish;
    public static GatewayDiscordClient client = null;
    public static TextChannel logChannel;
    public static Snowflake idCategoryJoin;
    public static Guild devarea;

    public static Snowflake idYes;
    public static Snowflake idNo;

    public static Snowflake idMissionsPayantes;
    public static Snowflake idMissionsGratuites;

    public static Snowflake idPresentation;
    public static Snowflake idRolesChannel;

    public static Snowflake idRoleRulesAccepted;
    public static Snowflake idWelcomChannel;

    public static Snowflake idGeneralChannel;

    public static Snowflake idModo;
    public static Snowflake idAdmin;

    public static Snowflake idNoMic;

    public static Snowflake idMeetupVerif;
    public static Snowflake idMeetupAnnonce;

    public static Snowflake idBump;

    public static Snowflake idPingMeetup;

    public static Snowflake idCategoryGeneral;

    public static BufferedImage backgroundXp;
    public static Snowflake idCommands;

    public static Snowflake idDevHelper;

    public static Snowflake idMissionsCategory;

    public static Snowflake idVoiceChannelHelp;

    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException {
        try {
            System.setOut(new PrintStream("out.txt"));
            System.setErr(new PrintStream("err.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        XpCount.init();
        CommandManager.init();
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();

            final File fileNextToJar = new File("./config.xml");

            if (!fileNextToJar.exists()) {
                final OutputStream out = new FileOutputStream(fileNextToJar);
                out.write(Main.class.getResource("/assets/config.xml").openStream().readAllBytes());
                out.close();
            }

            String token;
            Snowflake idDevArea;
            Snowflake idLogChannel;
            Snowflake idJoinLogChannel;

            while (true) {
                try {
                    final Document document = builder.parse(fileNextToJar);

                    token = new Scanner(new FileInputStream("./.token")).nextLine();
                    prefix = document.getElementsByTagName("prefix").item(0).getChildNodes().item(0).getNodeValue();
                    vanish = Boolean.parseBoolean(document.getElementsByTagName("vanish").item(0).getChildNodes().item(0).getNodeValue());
                    idDevArea = Snowflake.of(document.getElementsByTagName("idDevArea").item(0).getChildNodes().item(0).getNodeValue());
                    idLogChannel = Snowflake.of(document.getElementsByTagName("idLogChannel").item(0).getChildNodes().item(0).getNodeValue());
                    idJoinLogChannel = Snowflake.of(document.getElementsByTagName("idLogJoinChannel").item(0).getChildNodes().item(0).getNodeValue());
                    idCategoryJoin = Snowflake.of(document.getElementsByTagName("idJoinCategory").item(0).getChildNodes().item(0).getNodeValue());
                    idYes = Snowflake.of(document.getElementsByTagName("yes").item(0).getChildNodes().item(0).getNodeValue());
                    idNo = Snowflake.of(document.getElementsByTagName("no").item(0).getChildNodes().item(0).getNodeValue());

                    idMissionsGratuites = Snowflake.of(document.getElementsByTagName("missions-gratuites").item(0).getChildNodes().item(0).getNodeValue());
                    idMissionsPayantes = Snowflake.of(document.getElementsByTagName("mission-payantes").item(0).getChildNodes().item(0).getNodeValue());

                    idPresentation = Snowflake.of(document.getElementsByTagName("idPresentation").item(0).getChildNodes().item(0).getNodeValue());
                    idRolesChannel = Snowflake.of(document.getElementsByTagName("idRolesChannel").item(0).getChildNodes().item(0).getNodeValue());

                    idRoleRulesAccepted = Snowflake.of(document.getElementsByTagName("RulesAccepted").item(0).getChildNodes().item(0).getNodeValue());

                    idWelcomChannel = Snowflake.of(document.getElementsByTagName("idWelcomChannel").item(0).getChildNodes().item(0).getNodeValue());
                    idGeneralChannel = Snowflake.of(document.getElementsByTagName("idGeneral").item(0).getChildNodes().item(0).getNodeValue());

                    idModo = Snowflake.of(document.getElementsByTagName("idModo").item(0).getChildNodes().item(0).getNodeValue());
                    idAdmin = Snowflake.of(document.getElementsByTagName("idAdmin").item(0).getChildNodes().item(0).getNodeValue());

                    idNoMic = Snowflake.of(document.getElementsByTagName("noMic").item(0).getChildNodes().item(0).getNodeValue());

                    idMeetupVerif = Snowflake.of(document.getElementsByTagName("idMeetupVerif").item(0).getChildNodes().item(0).getNodeValue());
                    idMeetupAnnonce = Snowflake.of(document.getElementsByTagName("idMeetupAnnonce").item(0).getChildNodes().item(0).getNodeValue());

                    idBump = Snowflake.of(document.getElementsByTagName("idBump").item(0).getChildNodes().item(0).getNodeValue());

                    idPingMeetup = Snowflake.of(document.getElementsByTagName("idPingMeetup").item(0).getChildNodes().item(0).getNodeValue());

                    idCategoryGeneral = Snowflake.of(document.getElementsByTagName("idCategoryGeneral").item(0).getChildNodes().item(0).getNodeValue());

                    backgroundXp = ImageIO.read(Main.class.getResource("/assets/backgroundXp.jpg").openStream());
                    idCommands = Snowflake.of(document.getElementsByTagName("idCommands").item(0).getChildNodes().item(0).getNodeValue());

                    idDevHelper = Snowflake.of(document.getElementsByTagName("idDevHelper").item(0).getChildNodes().item(0).getNodeValue());

                    idMissionsCategory = Snowflake.of(document.getElementsByTagName("idMissionsCategory").item(0).getChildNodes().item(0).getNodeValue());

                    idVoiceChannelHelp = Snowflake.of(document.getElementsByTagName("idVoiceChannelHelp").item(0).getChildNodes().item(0).getNodeValue());

                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    final OutputStream out = new FileOutputStream(fileNextToJar);
                    out.write(Main.class.getResource("/assets/config.xml").openStream().readAllBytes());
                    out.close();
                }
            }

            client = DiscordClientBuilder.create(token)
                    .build()
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

            client.getEventDispatcher().on(VoiceStateUpdateEvent.class).subscribe(VoiceStateUpdate::VoiceStateUpdateFucntion);

            client.onDisconnect().block();

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

}