package devarea.bot.commands.created;

import devarea.bot.data.ColorsUsed;
import devarea.bot.automatical.XpCount;
import devarea.bot.commands.ShortCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;

public class LeaderBoard extends ShortCommand {

    public LeaderBoard(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        String text = "";
        Snowflake[] array = XpCount.getSortedMemberArray();
        for (int i = 0; i < 5; i++) {
            text += "`#" + (i + 1) + ":` <@" + array[i].asString() + ">: " + XpCount.getXpOf(array[i]) + "xp.\n";
        }
        text += "\n---------------------------------------------------------------\n\n";
        text += "`#" + XpCount.getRankOf(this.member.getId()) + ":` <@" + this.member.getId().asString() + ">: " + XpCount.getXpOf(this.member.getId()) + "xp.";
        sendEmbed(EmbedCreateSpec.builder()
                .title("LeaderBoard !")
                .color(ColorsUsed.same)
                .description(text).build(), false);
        this.endCommand();
    }
}
