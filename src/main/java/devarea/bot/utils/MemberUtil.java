package devarea.bot.utils;

import devarea.global.cache.MemberCache;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;

public class MemberUtil {

    static public String getMentionTextByMember(Member member) {
        return String.format("<@%s>", member.getId().asString());
    }

    static public String getMentionTextBySnowflake(Snowflake snowflake) {
        final Member member = MemberCache.get(snowflake.asString());
        return String.format("<@%s>", member.getId().asString());
    }

    static public Snowflake getSnowflakeByMentionText(String mentionText) {
        return Snowflake.of(mentionText.replace("<@", "").replace(">", ""));
    }

}
