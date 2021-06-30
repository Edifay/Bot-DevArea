package devarea.bot.event;

import devarea.bot.Init;
import devarea.bot.automatical.Joining;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;

import java.util.Map;

public class MemberLeave {

    public static void memberLeaveFunction(Snowflake finalIdDevArea, Snowflake finalIdJoinLogChannel, MemberLeaveEvent memberLeaveEvent) {
        ((TextChannel) Init.client.getGuildById(finalIdDevArea).block().getChannelById(finalIdJoinLogChannel).block()).createMessage(msg -> msg.setContent(memberLeaveEvent.getMember().get().getDisplayName() + " a quitter le serveur !")).subscribe();
        final Member member = memberLeaveEvent.getMember().get();
        for (Map.Entry<Snowflake, Joining> entry : MemberJoin.bindJoin.entrySet()) {
            final Snowflake id = entry.getKey();
            final Joining joining = entry.getValue();
            if (id.equals(member.getId()))
                joining.disconnect();
        }
    }
}
