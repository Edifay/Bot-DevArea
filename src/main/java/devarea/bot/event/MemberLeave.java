package devarea.bot.event;

import devarea.backend.controllers.rest.requestContent.RequestHandlerAuth;
import devarea.bot.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.automatical.XPHandler;
import devarea.bot.commands.CommandManager;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.object.entity.channel.TextChannel;

public class MemberLeave {

    private static TextChannel channel;

    public static void memberLeaveFunction(Snowflake finalIdDevArea, Snowflake finalIdJoinLogChannel,
                                           MemberLeaveEvent memberLeaveEvent) {

        MemberCache.slash(memberLeaveEvent.getUser().getId().asString());

        if (XPHandler.haveBeenSet(memberLeaveEvent.getUser().getId()))
            XPHandler.remove(memberLeaveEvent.getUser().getId());

        CommandManager.left(memberLeaveEvent.getUser().getId());
        RequestHandlerAuth.left(memberLeaveEvent.getUser().getId().asString());
        if (channel == null)
            channel =
                    (TextChannel) Init.client.getGuildById(finalIdDevArea).block().getChannelById(finalIdJoinLogChannel).block();
        channel.createMessage(msg -> msg.setContent(memberLeaveEvent.getMember().get().getDisplayName() + " a quitté" +
                " le serveur !")).subscribe();
    }
}
