package devarea.bot.utils;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;

public class ChannelUtil {

    public static TextChannel getTextChannelByMessage(Message message) {
        return (TextChannel) message.getChannel().block();
    }

}
