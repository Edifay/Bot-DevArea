package devarea.bot.event;

import devarea.bot.automatical.FreeLanceManager;
import devarea.bot.automatical.MissionsManager;
import devarea.bot.commands.CommandManager;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;

public class ButtonInteract {

    public static void ButtonInteractFunction(ButtonInteractionEvent event) {
        if (CommandManager.receiveInteract(event) || FreeLanceManager.interact(event) || MissionsManager.interact(event))
            ;
        event.deferEdit().subscribe(msg -> {
        }, err -> {
        });
    }

}
