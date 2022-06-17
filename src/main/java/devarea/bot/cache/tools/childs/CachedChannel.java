package devarea.bot.cache.tools.childs;

import devarea.bot.Init;
import devarea.bot.cache.tools.CachedObject;
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
        this.object_cached = Init.devarea.getChannelById(Snowflake.of(this.object_id)).block();
        return this.object_cached;
    }
}
