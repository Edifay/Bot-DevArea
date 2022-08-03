package devarea.bot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;

public abstract class ShortCommand extends Command {

    public ShortCommand() {
        super();
    }

    public ShortCommand(Member member) {
        super(member);
    }

    public ShortCommand(final Member member, final TextChannel channel) {
        super(member, channel);
    }

    public ShortCommand(final Member member, ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
    }
}
