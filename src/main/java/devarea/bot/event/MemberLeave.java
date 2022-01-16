package devarea.bot.event;

import devarea.bot.Init;
import devarea.bot.automatical.XpCount;
import devarea.bot.commands.CommandManager;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.object.entity.channel.TextChannel;

public class MemberLeave {

    private static TextChannel channel;

    public static void memberLeaveFunction(Snowflake finalIdDevArea, Snowflake finalIdJoinLogChannel, MemberLeaveEvent memberLeaveEvent) {

        if (XpCount.haveBeenSet(memberLeaveEvent.getUser().getId()))
            XpCount.remove(memberLeaveEvent.getUser().getId());

        synchronized (Init.membersId) {
            Init.membersId.remove(memberLeaveEvent.getUser().getId());
        }

        CommandManager.left(memberLeaveEvent.getUser().getId());
        if (channel == null)
            channel = (TextChannel) Init.client.getGuildById(finalIdDevArea).block().getChannelById(finalIdJoinLogChannel).block();
        channel.createMessage(msg -> msg.setContent(memberLeaveEvent.getMember().get().getDisplayName() + " a quitter le serveur !")).subscribe();
    }
}
