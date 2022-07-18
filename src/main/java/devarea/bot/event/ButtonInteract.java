package devarea.bot.event;

import devarea.bot.automatical.HelpRewardHandler;
import devarea.global.cache.MemberCache;
import devarea.global.handlers.FreeLanceHandler;
import devarea.global.handlers.MissionsHandler;
import devarea.bot.commands.CommandManager;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;

public class ButtonInteract {

    public static void ButtonInteractFunction(ButtonInteractionEvent event) {
        try {
            if (event.getInteraction().getMember().isPresent())
                MemberCache.use(event.getInteraction().getMember().get());
            if (CommandManager.receiveInteract(event) || MissionsHandler.interact(event) || HelpRewardHandler.react(event))
                ;
            event.deferEdit().subscribe(msg -> {
            }, err -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
