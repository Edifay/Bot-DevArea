package devarea.bot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;

public abstract class EndStape extends Stape {

    public EndStape(Stape... stapes) {
        this.stapes = stapes;
    }

    @Override
    protected boolean onReceiveMessage(MessageCreateEvent event) {
        return end;
    }

    @Override
    protected boolean onReceiveReact(ReactionAddEvent event) {
        return end;
    }
}
