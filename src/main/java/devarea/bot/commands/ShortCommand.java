package devarea.bot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;

public abstract class ShortCommand extends Command {

    public ShortCommand(final MessageCreateEvent message) {
        super(message);
    }

    @Override
    protected Boolean endCommand() {
        ended = true;
        return super.endCommand();
    }
}
