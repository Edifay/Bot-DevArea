package devarea.bot.commands.created;

import devarea.bot.data.ColorsUsed;
import devarea.bot.automatical.XpCount;
import devarea.bot.commands.ShortCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class LeaderBoard extends ShortCommand {

    public LeaderBoard(MessageCreateEvent message) {
        super(message);
        sendEmbed(embedCreateSpec -> {
            embedCreateSpec.setTitle("LeaderBoard !");
            embedCreateSpec.setColor(ColorsUsed.same);
            String text = "";
            Snowflake[] array = XpCount.getSortedMemberArray();
            for (int i = 0; i < 5; i++) {
                text += "`#" + (i + 1) + ":` <@" + array[i].asString() + ">: " + XpCount.getXpOf(array[i]) + "xp.\n";
            }
            text += "\n---------------------------------------------------------------\n\n";
            text += "`#" + XpCount.getRankOf(this.member.getId()) + ":` <@" + this.member.getId().asString() + ">: " + XpCount.getXpOf(this.member.getId()) + "xp.";
            embedCreateSpec.setDescription(text);
        }, false);
        this.endCommand();
    }
}
