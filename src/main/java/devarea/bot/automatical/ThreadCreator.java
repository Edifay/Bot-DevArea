package devarea.bot.automatical;

import devarea.bot.Init;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.StartThreadSpec;

public class ThreadCreator {

    public static void newMessage(MessageCreateEvent message) {
        if (Init.initial.channelsThreadCreator.contains(message.getMessage().getChannelId()) && message.getMember().isPresent()) {
            message.getMessage().startThread(StartThreadSpec.builder()
                    .name(message.getMember().get().getDisplayName() + " - " + message.getMessage().getContent()
                            .substring(0, Math.min(message.getMessage().getContent().length(),
                                    97 - message.getMember().get().getDisplayName().length())))
                    .build()).subscribe();
        }
    }


}
