package devarea.bot.commands.inLine;

import devarea.bot.presets.ColorsUsed;
import devarea.bot.commands.ShortCommand;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;

public class Ping extends ShortCommand {

    public Ping(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        this.sendEmbed(EmbedCreateSpec.builder()
                .title("Pong !")
                .description("La latence avec le bot est de `" + (System.currentTimeMillis() - message.getTimestamp().toEpochMilli()) + "`ms.")
                .color(ColorsUsed.same).build(), false);
        this.endCommand();
    }
}
