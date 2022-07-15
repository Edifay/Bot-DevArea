package devarea.bot.commands.inLine;

import devarea.bot.presets.ColorsUsed;
import devarea.global.handlers.XPHandler;
import devarea.bot.commands.ShortCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;

public class LeaderBoard extends ShortCommand {

    public LeaderBoard(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        String text = "";
        Snowflake[] array = XPHandler.getSortedMemberArray();
        for (int i = 0; i < 5; i++) {
            text += "`#" + (i + 1) + ":` <@" + array[i].asString() + ">: " + XPHandler.getXpOfMember(array[i]) + "xp.\n";
        }
        text += "\n---------------------------------------------------------------\n\n";
        text += "`#" + XPHandler.getRankOfMember(this.member.getId()) + ":` <@" + this.member.getId().asString() + ">: " + XPHandler.getXpOfMember(this.member.getId()) + "xp.";
        text += "\n\nVous pouvez retrouver le leaderboard en entier sur le site web : https://devarea.fr/stats.";
        sendEmbed(EmbedCreateSpec.builder()
                .title("LeaderBoard !")
                .color(ColorsUsed.same)
                .description(text).build(), false);
        this.endCommand();
    }
}
