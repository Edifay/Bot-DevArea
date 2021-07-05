package devarea.bot.event;

import devarea.bot.Init;
import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.entity.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageUpdate {
    public static void messageUpdateFunction(MessageUpdateEvent messageUpdateEvent) {
        try {
            final Message message = messageUpdateEvent.getMessage().block();

            if (message.getAuthor().get().isBot() || messageUpdateEvent.getGuildId().isEmpty())
                return;

            Init.logChannel.createMessage(msg -> msg.setEmbed(embed -> {
                final DateTimeFormatter hours = DateTimeFormatter.ofPattern("HH:mm");
                final DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                final LocalDateTime now = LocalDateTime.now();
                embed.setColor(ColorsUsed.same);
                embed.setTitle(message.getAuthor().get().getTag() + " a éditer un message :");
                embed.setDescription(message.getContent());
                embed.setFooter(date.format(now) + " at " + hours.format(now) + ".", message.getAuthor().get().getAvatarUrl());
            })).subscribe();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
