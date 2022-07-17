package devarea.bot.event;

import devarea.bot.automatical.RunHandler;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.object.entity.Message;

public class MessageDelete {

    public static void messageDeleteFunction(MessageDeleteEvent messageDeleted) {
        try {
            if(messageDeleted.getMessage().isEmpty())
                return;

            final Message message = messageDeleted.getMessage().get();

            if (message.getAuthor().isEmpty() || message.getAuthor().get().isBot() || messageDeleted.getGuild().block() == null)
                return;

            RunHandler.onDelete(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
