package devarea.bot.event;

import devarea.bot.automatical.RoleMenuHandler;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;

public class SelectMenuInteraction {

    public static void SelectMenuInteractionFunction(SelectMenuInteractionEvent event) {
        RoleMenuHandler.onReceiveMenuSelect(event);
    }
}
