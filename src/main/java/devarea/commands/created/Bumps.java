package devarea.commands.created;

import devarea.Data.ColorsUsed;
import devarea.automatical.Bump;
import devarea.commands.ShortCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class Bumps extends ShortCommand {
    public Bumps(MessageCreateEvent message) {
        super(message);
        sendEmbed(embedCreateSpec -> {
            embedCreateSpec.setTitle("Bump-Count:");
            embedCreateSpec.setColor(ColorsUsed.same);
            String text = "";
            Snowflake[] array = Bump.getSortedMemberArray();
            for (int i = 0; i < 5; i++)
                text += "`#" + (i + 1) + ":` <@" + array[i].asString() + ">: " + Bump.getBumpsOf(array[i]) + " bumps.\n";

            text += "\n---------------------------------------------------------------\n\n";
            text += "`#" + Bump.getRankOf(this.member.getId()) + ":` <@" + this.member.getId().asString() + ">: " + Bump.getBumpsOf(this.member.getId()) + " bumps.";
            embedCreateSpec.setDescription(text);
        });
    }
}
