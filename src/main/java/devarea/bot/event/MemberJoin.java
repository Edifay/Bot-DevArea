package devarea.bot.event;

import devarea.Main;
import devarea.bot.cache.ChannelCache;
import devarea.bot.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.ConsumableCommand;
import devarea.bot.commands.outLine.JoinCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.channel.TextChannel;

public class MemberJoin {

    private static TextChannel channelJoin;

    public static void memberJoinFunction(MemberJoinEvent event) {
        MemberCache.use(event.getMember());

        if (channelJoin == null)
            channelJoin = (TextChannel) ChannelCache.fetch(Init.initial.logJoin_channel.asString());
        channelJoin.createMessage(msg -> msg.setContent(event.getMember().getDisplayName() + " a rejoint le serveur " +
                "!")).subscribe();

        if (!Main.developing)
            CommandManager.addManualCommand(event.getMember(), new ConsumableCommand(JoinCommand.class) {
                @Override
                protected Command command() {
                    return new JoinCommand(event.getMember());
                }
            });
    }

}
