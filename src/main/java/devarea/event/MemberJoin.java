package devarea.event;

import devarea.Main;
import devarea.automatical.Joining;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;

import java.util.HashMap;

public class MemberJoin {

    public final static HashMap<Snowflake, Joining> bindJoin = new HashMap<>();

    public static void join(final MemberJoinEvent event){
        final Member member = event.getMember();
        bindJoin.put(member.getId(), new Joining(member));
    }

    public static void join(final Member member){
        bindJoin.put(member.getId(), new Joining(member));
    }


    public static void memberJoinFunction(Snowflake finalIdDevArea, Snowflake finalIdJoinLogChannel, MemberJoinEvent memberJoinEvent) {
        ((TextChannel) Main.client.getGuildById(finalIdDevArea).block().getChannelById(finalIdJoinLogChannel).block()).createMessage(msg -> msg.setContent(memberJoinEvent.getMember().getDisplayName() + " a rejoins le serveur !")).block();
        MemberJoin.join(memberJoinEvent);
    }

}
