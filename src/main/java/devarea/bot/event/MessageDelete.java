package devarea.bot.event;

import devarea.bot.Init;
import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.object.entity.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageDelete {

    public static void messageDeleteFunction(MessageDeleteEvent messageDeleted) {
        try {
            if(messageDeleted.getMessage().isEmpty())
                return;
            final Message message = messageDeleted.getMessage().get();
            if (message.getAuthor().get().isBot() || messageDeleted.getGuild().block() == null)
                return;

            Init.logChannel.createMessage(msg -> msg.setEmbed(embed -> {
                final DateTimeFormatter hours = DateTimeFormatter.ofPattern("HH:mm");
                final DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                final LocalDateTime now = LocalDateTime.now();
                embed.setColor(ColorsUsed.same);
                embed.setTitle("Le message de " + message.getAuthor().get().getTag() + " a été supprimé :");
                embed.setDescription(message.getContent());
                embed.setFooter(date.format(now) + " at " + hours.format(now) + ".", message.getAuthor().get().getAvatarUrl());
            })).subscribe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
