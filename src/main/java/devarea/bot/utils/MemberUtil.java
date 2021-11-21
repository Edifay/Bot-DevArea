package devarea.bot.utils;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;

public class MemberUtil {

    static public String getMentionTextByMember(Member member) {
        return String.format("<@%s>", member.getId().asString());
    }

    static public Snowflake getSnowflakeByMentionText(String mentionText) {
        return Snowflake.of(mentionText.replace("<@", "").replace(">", ""));
    }

}
