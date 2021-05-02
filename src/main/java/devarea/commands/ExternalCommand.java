package devarea.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;

public class ExternalCommand extends Command {

    public ExternalCommand(MessageCreateEvent event) {
        super(event);
    }

    public ExternalCommand(ReactionAddEvent event) {
        super(event);
    }

    @Override
    protected Boolean endCommand() {
        if (this.reaction != null)
            CommandManager.actualCommands.remove(this.reaction.getMember().get().getId());
        else if (this.message != null)
            super.endCommand();
        return ended;
    }
}
