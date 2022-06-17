package devarea.global.cache.tools.childs;

import devarea.bot.Init;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.tools.CachedObject;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.GuildChannel;

public class CachedChannel extends CachedObject<GuildChannel> {

    public CachedChannel(GuildChannel channel, long last_fetch) {
        super(channel, channel.getId().asString(), last_fetch);
    }

    public CachedChannel(String channelID) {
        super(channelID);
    }

    @Override
    public GuildChannel fetch() {
        try {
            this.object_cached = Init.devarea.getChannelById(Snowflake.of(this.object_id)).block();
        } catch (Exception e) {
            System.err.println("ERROR: Channel couldn't be fetched !");
            this.object_cached = null;
        }

        if (this.object_cached == null) {
            ChannelCache.slash(this.object_id);
            return null;
        }

        this.last_fetch = System.currentTimeMillis();
        return this.object_cached;
    }
}
