package devarea.bot.event;

import devarea.bot.automatical.RoleMenuHandler;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;

public class SelectMenuInteraction {

    public static void SelectMenuInteractionFunction(SelectMenuInteractionEvent event) {
        try {
            if (RoleMenuHandler.onReceiveMenuSelect(event) /*|| EventMembers.onSelectColor(event)*/) return;
            event.reply(InteractionApplicationCommandCallbackSpec.builder()
                            .ephemeral(true)
                            .content("Il n'y a aucune action sur ce menu !")
                    .build()).subscribe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
