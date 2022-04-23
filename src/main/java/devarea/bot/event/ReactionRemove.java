package devarea.bot.event;

import discord4j.core.event.domain.message.ReactionRemoveEvent;

import static devarea.bot.automatical.RolesReactsHandler.onRemoveReact;

public class ReactionRemove {

    public static void FunctionReactionRemoveEvent(ReactionRemoveEvent event){
        onRemoveReact(event);
    }
}
