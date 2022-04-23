package devarea.bot.event;

import devarea.bot.cache.MemberCache;
import devarea.bot.automatical.FreeLanceHandler;
import devarea.bot.automatical.MissionsHandler;
import devarea.bot.commands.CommandManager;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;

public class ButtonInteract {

    public static void ButtonInteractFunction(ButtonInteractionEvent event) {
        if (event.getInteraction().getMember().isPresent())
            MemberCache.use(event.getInteraction().getMember().get());
        if (CommandManager.receiveInteract(event) || FreeLanceHandler.interact(event)
                || MissionsHandler.interact(event)) ;
        event.deferEdit().subscribe(msg -> {
        }, err -> {
        });
    }

}
