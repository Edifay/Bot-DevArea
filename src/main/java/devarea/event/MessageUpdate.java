package devarea.event;

import devarea.Main;
import devarea.Data.ColorsUsed;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.entity.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageUpdate {
    public static void messageUpdateFunction(MessageUpdateEvent messageUpdateEvent) {
        try {
            final Message message = messageUpdateEvent.getMessage().block();

            if (message.getAuthor().get().isBot() || message.getGuild().block() == null)
                return;

            Main.logChannel.createMessage(msg -> msg.setEmbed(embed -> {
                final DateTimeFormatter hours = DateTimeFormatter.ofPattern("HH:mm");
                final DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                final LocalDateTime now = LocalDateTime.now();
                embed.setColor(ColorsUsed.same);
                embed.setTitle(message.getAuthor().get().getTag() + " a éditer un message :");
                embed.setDescription(message.getContent());
                embed.setFooter(date.format(now) + " at " + hours.format(now) + ".", message.getAuthor().get().getAvatarUrl());
            })).block();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
