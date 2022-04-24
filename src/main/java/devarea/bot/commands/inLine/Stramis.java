package devarea.bot.commands.inLine;

import devarea.bot.commands.ShortCommand;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;

public class Stramis extends ShortCommand {

    public Stramis() {

    }

    public Stramis(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        send(MessageCreateSpec.builder()
                .content("https://tenor.com/view/happy-tgif-drinking-drunk-dance-gif-5427975")
                .build(), false);
        this.endCommand();
    }

}
