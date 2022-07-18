package devarea.global.handlers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import devarea.global.cache.RoleCache;
import devarea.global.cache.tools.childs.CachedRole;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.VoiceChannelEditSpec;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static devarea.global.utils.ThreadHandler.repeatEachMillis;
import static devarea.global.utils.ThreadHandler.startAwayIn;

public class StatsHandler {

    private static final HashMap<Role, VoiceChannel> roleBoundToChannel = new HashMap<>();
    private static VoiceChannel channelMemberCount;

    /*
        Initialise StatsHandler
     */
    public static void init() throws IOException {
        final File fileNextToJar = new File("./stats.json");
        ObjectMapper mapper = new ObjectMapper();

        if (!fileNextToJar.exists())
            mapper.writeValue(fileNextToJar, new StatsConfig());

        StatsConfig config = mapper.readValue(fileNextToJar, new TypeReference<>() {
        });

        if (config.idMemberChannel == null) {
            System.err.println("You need to configure stats.json !");
            return;
        }

        // Transform config

        channelMemberCount = (VoiceChannel) ChannelCache.get(config.idMemberChannel);
        config.rolesToChannels.forEach((k, v) -> {
            roleBoundToChannel.put(RoleCache.get(k), (VoiceChannel) ChannelCache.get(v));
        });

    }

    /*
        Start loop update
     */
    public static void start() {
        startAwayIn(() -> repeatEachMillis(StatsHandler::update, 600000, false), 600000, false);
    }

    /*
        Rename all channels with the role count
     */
    public static void update() {
        channelMemberCount.edit(VoiceChannelEditSpec.builder().name("Members: " + MemberCache.cacheSize())
                .build()).subscribe();

        roleBoundToChannel.forEach((role, channel) -> {
            channel.edit(VoiceChannelEditSpec.builder().name(role.getName() + ": " +
                    CachedRole.getRoleMemberCount(role.getId().asString())).build()).subscribe();
        });
    }

    public static class StatsConfig {
        @JsonProperty
        protected String idMemberChannel;
        @JsonProperty
        protected HashMap<String, String> rolesToChannels;
    }

}
