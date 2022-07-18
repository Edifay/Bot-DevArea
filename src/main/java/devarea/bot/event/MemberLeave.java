package devarea.bot.event;

import devarea.backend.controllers.rest.requestContent.RequestHandlerAuth;
import devarea.global.handlers.FreeLanceHandler;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import devarea.bot.Init;
import devarea.global.handlers.UserDataHandler;
import devarea.global.handlers.XPHandler;
import devarea.bot.commands.CommandManager;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.object.entity.channel.TextChannel;

public class MemberLeave {


    public static void memberLeaveFunction(MemberLeaveEvent memberLeaveEvent) {
        try {
            MemberCache.slash(memberLeaveEvent.getUser().getId().asString());

            CommandManager.left(memberLeaveEvent.getUser().getId());
            RequestHandlerAuth.left(memberLeaveEvent.getUser().getId().asString());
            UserDataHandler.left(memberLeaveEvent.getUser().getId().asString());
            ((TextChannel) ChannelCache.watch(Init.initial.logJoin_channel.asString()))
                    .createMessage(msg -> msg.setContent(memberLeaveEvent.getMember().get().getDisplayName() + " a " +
                            "quitté le serveur !")).subscribe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
