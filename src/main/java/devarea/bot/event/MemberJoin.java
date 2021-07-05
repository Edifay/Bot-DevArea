package devarea.bot.event;

import devarea.bot.Init;
import devarea.bot.automatical.XpCount;
import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.ConsumableCommand;
import devarea.bot.commands.with_out_text_starter.JoinCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.channel.TextChannel;

public class MemberJoin {

    private static TextChannel channelJoin;

    public static void memberJoinFunction(Snowflake finalIdDevArea, Snowflake finalIdJoinLogChannel, MemberJoinEvent event) {
        if (channelJoin == null)
            channelJoin = (TextChannel) Init.client.getGuildById(finalIdDevArea).block().getChannelById(finalIdJoinLogChannel).block();
        channelJoin.createMessage(msg -> msg.setContent(event.getMember().getDisplayName() + " a rejoins le serveur !")).subscribe();

        XpCount.addNewMember(event.getMember().getId());
        synchronized (Init.membersId) {
            Init.membersId.add(event.getMember().getId());
        }
        CommandManager.addManualCommand(event.getMember().getId(), new ConsumableCommand() {
            @Override
            protected Command command() {
                return new JoinCommand(event.getMember());
            }
        });
    }

}
