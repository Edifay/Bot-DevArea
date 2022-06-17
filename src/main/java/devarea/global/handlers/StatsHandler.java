package devarea.global.handlers;

import devarea.bot.Init;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import devarea.global.cache.tools.childs.CachedMember;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.VoiceChannelEditSpec;
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

public class StatsHandler {

    private static final HashMap<Role, VoiceChannel> roleBoundToChannel = new HashMap<>();
    private static VoiceChannel channelMemberCount;

    public static void init() throws IOException {
        final File fileNextToJar = new File("./stats.xml");

        if (!fileNextToJar.exists()) {
            final OutputStream out = new FileOutputStream(fileNextToJar);
            out.write(Init.class.getResource("/assets/stats.xml").openStream().readAllBytes());
            out.close();
        }
        try {
            final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileNextToJar);
            NodeList list = document.getElementsByTagName("stats").item(0).getChildNodes();
            channelMemberCount =
                    (VoiceChannel) ChannelCache.fetch(document.getElementsByTagName("member").item(0).getChildNodes().item(0).getNodeValue());

            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node instanceof Element && !node.getNodeName().equals("member")) {
                    Element el = (Element) node;
                    roleBoundToChannel.put(Init.devarea.getRoleById(Snowflake.of(el.getElementsByTagName("role").item(0).getChildNodes().item(0).getNodeValue())).block(), (VoiceChannel) ChannelCache.fetch(el.getElementsByTagName("channel").item(0).getChildNodes().item(0).getNodeValue()));
                }
            }
        } catch (Exception e) {
            final OutputStream out = new FileOutputStream(fileNextToJar);
            out.write(Init.class.getResource("/assets/stats.xml").openStream().readAllBytes());
            out.close();
        }

    }

    public static void start() {
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(600000);
                    try {
                        update();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
            }
        }).start();
    }

    public static void update() {
        final HashMap<VoiceChannel, Integer> channelCount = new HashMap<>();
        roleBoundToChannel.forEach((role, channel) -> channelCount.put(channel, 0));

        channelMemberCount.edit(VoiceChannelEditSpec.builder()
                .name("Members: " + MemberCache.cacheSize())
                .build()).subscribe();

        for (CachedMember member : MemberCache.cache().values())
            roleBoundToChannel.forEach((role, channel) -> {
                if (member.watch().getRoleIds().contains(role.getId()))
                    channelCount.put(channel, channelCount.get(channel) + 1);
            });

        roleBoundToChannel.forEach((role, channel) -> channel.edit(VoiceChannelEditSpec.builder()
                .name(role.getName() + ": " + channelCount.get(channel))
                .build()).subscribe());
    }
}
