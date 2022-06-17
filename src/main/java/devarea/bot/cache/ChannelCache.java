package devarea.bot.cache;

import devarea.bot.cache.tools.childs.CachedChannel;
import devarea.bot.cache.tools.childs.CachedRole;
import discord4j.core.object.entity.channel.GuildChannel;

import java.util.HashMap;

public class ChannelCache {

    public static final HashMap<String, CachedChannel> channels = new HashMap<>();

    public static GuildChannel get(final String channelID) {
        CachedChannel cachedChannel = getCachedChannel(channelID);
        if (cachedChannel == null) {
            cachedChannel = new CachedChannel(channelID);
            channels.put(channelID, cachedChannel);
        }

        return cachedChannel.get();
    }

    public static GuildChannel fetch(final String channelID) {
        CachedChannel cachedChannel = getCachedChannel(channelID);
        if (cachedChannel == null) {
            cachedChannel = new CachedChannel(channelID);
            channels.put(channelID, cachedChannel);
        }

        return cachedChannel.fetch();
    }

    public static GuildChannel watch(final String channelID) {
        CachedChannel cachedChannel = getCachedChannel(channelID);
        if (cachedChannel == null) {
            cachedChannel = new CachedChannel(channelID);
            cachedChannel.fetch();
            channels.put(channelID, cachedChannel);
        }

        return cachedChannel.watch();
    }

    private static CachedChannel getCachedChannel(final String channelID) {
        return channels.get(channelID);
    }


}
