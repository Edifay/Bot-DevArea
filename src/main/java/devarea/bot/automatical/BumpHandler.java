package devarea.bot.automatical;

import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.ChannelCache;
import discord4j.common.util.Snowflake;
import discord4j.common.util.TimestampFormat;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.concurrent.TimeUnit;

public class BumpHandler {
    /*
        7200000ms -> 2hours
     */
    private static final long BUMP_DELAY = 7200000;

    private static final TextChannel bumpChannel =
            (TextChannel) ChannelCache.watch(Init.initial.bump_channel.asString());
    private static Thread thread;
    private static Message botMessage;

    public static void init() {
        if (bumpChannel != null) {
            botMessage = bumpChannel.getMessagesBefore(Snowflake.of(Instant.now()))
                    .skipUntil(message -> message.getAuthor().isPresent()
                            && message.getAuthor().get().getId().equals(Init.client.getSelfId()))
                    .blockFirst();

            checkBumpAvailable();
        }
    }

    private synchronized static void sendBumpMessage(Instant instant) {
        boolean available = instant.isBefore(Instant.now());

        EmbedCreateSpec spec = available ?
                EmbedCreateSpec.builder()
                        .color(ColorsUsed.same)
                        .description("Le bump est disponible avec la commande `/bump`.")
                        .build() :
                EmbedCreateSpec.builder()
                        .color(ColorsUsed.wrong)
                        .description("Le bump est à nouveau disponible " + TimestampFormat.RELATIVE_TIME.format(instant) + ".")
                        .build();

        if (botMessage != null) {
            botMessage.delete().subscribe();
        }
        botMessage = Command.send(bumpChannel, MessageCreateSpec.create().withEmbeds(spec), true);

        waitUntilBumpAvailable(instant);
    }

    public static void checkBumpAvailable() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        sendBumpMessage(nextBump());
    }

    private static boolean isDisboardBump(Message message) {
        return message.getAuthor().isPresent()
                && message.getAuthor().get().getId().equals(Init.initial.disboard_bot)
                && message.getInteraction().isPresent()
                && message.getInteraction().get().getName().equals("bump")
                || message.getTimestamp().isBefore(Instant.now().minusMillis(BUMP_DELAY));
    }

    private static Instant nextBump() {
        Message disboardMessage = latestDisboardMessage();
        long latestBump = BUMP_DELAY - (disboardMessage != null ?
                Duration.between(disboardMessage.getTimestamp(), Instant.now()).toMillis() : 0);
        return Instant.now().plusMillis(latestBump);
    }

    private synchronized static Message latestDisboardMessage() {
        if (bumpChannel == null) {
            return null;
        }
        Snowflake now = Snowflake.of(Instant.now());
        return bumpChannel.getMessagesBefore(now)
                .skipUntil(BumpHandler::isDisboardBump)
                .blockFirst();
    }

    private static void waitUntilBumpAvailable(Instant instant) {
        long millis = Duration.between(Instant.now(), instant).toMillis();
        if (millis >= 0) {
            thread = new Thread(() -> {
                try {
                    Thread.sleep(millis);
                    sendBumpMessage(nextBump());
                } catch (InterruptedException ignored) {

                }
            });
            thread.start();
        }
    }
}
