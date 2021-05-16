package devarea.commands;

import devarea.commands.Stape;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;

public abstract class EndStape extends Stape {
    @Override
    protected boolean onReceiveMessage(MessageCreateEvent event) {
        return end;
    }

    @Override
    protected boolean onReceiveReact(ReactionAddEvent event) {
        return end;
    }
}
