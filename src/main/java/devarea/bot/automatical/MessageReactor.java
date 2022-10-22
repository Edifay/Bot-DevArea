package devarea.bot.automatical;

import devarea.bot.Init;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.Objects;

public class MessageReactor {

    public static void onMessage(MessageCreateEvent event) {
        if (messageContain(event, "devarea") || messageContain(event, "dev'area") || messageContain(event,
                "dev area")) {
            event.getMessage().addReaction(ReactionEmoji.custom(Objects.requireNonNull(Init.devarea.getGuildEmojiById(Snowflake.of(
                    "983423296341176331")).block()))).subscribe();
        }
        if (messageContain(event, "salut") || messageContain(event, "coucou") || messageContain(event,
                "hey") || messageContain(event, "bonjour") || messageContain(event, "hello")) {
            event.getMessage().addReaction(ReactionEmoji.unicode("👋")).subscribe();
        }
        if (messageContain(event, "pour quoi") || messageContain(event, "pourquoi") || messageContain(event, "comment"
        )) {
            event.getMessage().addReaction(ReactionEmoji.unicode("🤔")).subscribe();
        }
        if (messageContain(event, "merci")) {
            event.getMessage().addReaction(ReactionEmoji.unicode("🙏")).subscribe();
        }
    }

    private static boolean messageContain(MessageCreateEvent event, String word) {
        return event.getMessage().getContent().toLowerCase().contains(word);
    }
}
