package devarea.bot.commands;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;

public abstract class EndStep extends Step {

    public EndStep(Step... steps) {
        this.steps = steps;
    }

    @Override
    protected boolean onReceiveMessage(MessageCreateEvent event) {
        return end;
    }

    @Override
    protected boolean onReceiveReact(ReactionAddEvent event) {
        return end;
    }

    protected boolean onReceiveInteract(ButtonInteractionEvent event) {
        return end;
    }
}
