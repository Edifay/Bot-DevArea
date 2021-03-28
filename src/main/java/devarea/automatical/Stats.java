package devarea.automatical;

import devarea.Main;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.VoiceChannel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

public class Stats {

    private static final HashMap<Role, VoiceChannel> roleBoundToChannel = new HashMap<>();
    private static VoiceChannel channelMemberCount;

    public static void init() throws IOException {
        final File fileNextToJar = new File("./stats.xml");

        if (!fileNextToJar.exists()) {
            final OutputStream out = new FileOutputStream(fileNextToJar);
            out.write(Main.class.getResource("/assets/stats.xml").openStream().readAllBytes());
            out.close();
        }
        try {
            final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileNextToJar);
            NodeList list = document.getElementsByTagName("stats").item(0).getChildNodes();
            channelMemberCount = (VoiceChannel) Main.devarea.getChannelById(Snowflake.of(document.getElementsByTagName("member").item(0).getChildNodes().item(0).getNodeValue())).block();

            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node instanceof Element && !node.getNodeName().equals("member")) {
                    Element el = (Element) node;
                    roleBoundToChannel.put(Main.devarea.getRoleById(Snowflake.of(el.getElementsByTagName("role").item(0).getChildNodes().item(0).getNodeValue())).block(), (VoiceChannel) Main.devarea.getChannelById(Snowflake.of(el.getElementsByTagName("channel").item(0).getChildNodes().item(0).getNodeValue())).block());
                    System.out.println("Element : " + el.getNodeName());
                }
            }
        } catch (Exception e) {
            final OutputStream out = new FileOutputStream(fileNextToJar);
            out.write(Main.class.getResource("/assets/stats.xml").openStream().readAllBytes());
            out.close();
        }

    }

    public static void start() {
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(600000);
                    update();
                }
            } catch (InterruptedException e) {
            }
        }).start();
    }

    public static void update() {
        final HashMap<VoiceChannel, Integer> channelCount = new HashMap<>();
        roleBoundToChannel.forEach((role, channel) -> channelCount.put(channel, 0));

        List<Member> members = Main.devarea.getMembers().buffer().blockLast();
        channelMemberCount.edit(voiceChannelEditSpec -> voiceChannelEditSpec.setName("Member: " + members.size())).block();

        for (Member member : members)
            roleBoundToChannel.forEach((role, channel) -> {
                if (member.getRoleIds().contains(role.getId()))
                    channelCount.put(channel, channelCount.get(channel) + 1);
            });

        roleBoundToChannel.forEach((role, channel) -> channel.edit(voiceChannelEditSpec -> voiceChannelEditSpec.setName(role.getName() + ": " + channelCount.get(channel))).block());
    }
}
