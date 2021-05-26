package devarea.commands.created;

import devarea.data.ColorsUsed;
import devarea.automatical.XpCount;
import devarea.commands.ShortCommand;
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
        });
        this.endCommand();
    }
}
