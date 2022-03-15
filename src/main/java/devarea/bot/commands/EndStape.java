package devarea.bot.commands;

import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;

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

    protected boolean onReceiveInteract(ButtonInteractionEvent event) {
        return end;
    }
}
