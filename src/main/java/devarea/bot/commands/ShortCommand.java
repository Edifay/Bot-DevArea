package devarea.bot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;

public abstract class ShortCommand extends Command {

    public ShortCommand() {
        super();
    }

    public ShortCommand(final Member member, final TextChannel channel) {
        super(member, channel);
    }

    @Override
    protected Boolean endCommand() {
        return super.endCommand();
    }
}
