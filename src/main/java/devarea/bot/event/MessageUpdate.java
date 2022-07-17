package devarea.bot.event;

import devarea.bot.automatical.RunHandler;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.entity.Message;

public class MessageUpdate {
    public static void messageUpdateFunction(MessageUpdateEvent messageUpdateEvent) {
        try {
            final Message message = messageUpdateEvent.getMessage().block();

            if (message == null || message.getAuthor().isEmpty() || message.getAuthor().get().isBot())
                return;

            RunHandler.onEdit(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
