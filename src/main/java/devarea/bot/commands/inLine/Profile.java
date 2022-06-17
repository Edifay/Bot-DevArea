package devarea.bot.commands.inLine;

import devarea.bot.automatical.EmbedLinkHandler;
import devarea.global.cache.MemberCache;
import devarea.bot.commands.ShortCommand;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;

import java.util.ArrayList;
import java.util.Collections;

public class Profile extends ShortCommand {

    public Profile(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        Member pinged = member;
        try {
            pinged = MemberCache.get(message.getUserMentions().get(0).getId().asString());
        } catch (Exception e) {
        } finally {
            EmbedLinkHandler.generateLinkEmbed(channel, new ArrayList<Member>(Collections.singleton(pinged)));
        }
    }
}
