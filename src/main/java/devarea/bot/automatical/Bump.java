package devarea.bot.automatical;

import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;

import java.util.function.Consumer;

public class Bump {

    private static long dateToBump;
    private static Message message;
    private static TextChannel channel;

    public static void init() {
        channel = (TextChannel) Init.devarea.getChannelById(Init.idBump).block();
        message = Command.sendEmbed(channel, embedCreateSpec -> {
            embedCreateSpec.setColor(ColorsUsed.wrong);
            embedCreateSpec.setDescription("Le bot vien de s'initialisé utilisez la commande `!d bump`, pour lancer le compte à rebours.");
        }, true);
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(60000);
                    try {
                        restartIfNotTheLast();
                        if (dateToBump - System.currentTimeMillis() > 0) {
                            if (!message.getEmbeds().get(0).getDescription().get().equals("Le bump est à nouveau disponible dans " + (int) ((dateToBump - System.currentTimeMillis()) / 60000L) + "minutes."))
                                edit(msg -> msg.setEmbed(embed -> {
                                    embed.setDescription("Le bump est à nouveau disponible dans " + (int) ((dateToBump - System.currentTimeMillis()) / 60000L) + "minutes.");
                                    embed.setColor(ColorsUsed.wrong);
                                }));
                        } else if (!message.getEmbeds().get(0).getDescription().get().equals("Le bump est disponible avec la commande `!d bump`.") && !message.getEmbeds().get(0).getDescription().get().equals("Le bot vien de s'initialisé utilisez la commande `!d bump`, pour lancer le compte à rebours."))
                            replace(msg -> msg.setEmbed(embed -> {
                                embed.setDescription("Le bump est disponible avec la commande `!d bump`.");
                                embed.setColor(ColorsUsed.same);
                            }));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }).start();

    }

    public synchronized static void getDisboardMessage(MessageCreateEvent event) {
        if (!event.getMessage().getChannel().block().getId().equals(channel.getId()))
            return;

        String[] coupes = event.getMessage().getEmbeds().get(0).getDescription().get().split(" ");

        if (coupes.length == 0)
            return;

        if (coupes[1].equalsIgnoreCase("attendez")) {
            dateToBump = System.currentTimeMillis() + (Integer.parseInt(coupes[3]) * 60000L);
            replace(msg -> msg.setEmbed(embed -> {
                embed.setDescription("Le bump est à nouveau disponible dans " + (int) ((dateToBump - System.currentTimeMillis()) / 60000L) + "minutes.");
                embed.setColor(ColorsUsed.wrong);
            }));
        } else if (event.getMessage().getEmbeds().get(0).getDescription().get().contains("effectué")) {
            dateToBump = System.currentTimeMillis() + (120 * 60000L);
            replace(msg -> msg.setEmbed(embed -> {
                embed.setDescription("Le bump est à nouveau disponible dans " + 120 + "minutes.");
                embed.setColor(ColorsUsed.wrong);
            }));
        }
    }

    private synchronized static void replace(final Consumer<? super MessageCreateSpec> spec) {
        Command.delete(false, message);
        message = Command.send(channel, spec, true);
    }

    private synchronized static void edit(final Consumer<? super MessageEditSpec> spec) {
        message = message.edit(spec).block();
    }

    public static void messageInChannel(MessageCreateEvent event) {
        if (!event.getMessage().getContent().equalsIgnoreCase("!d bump"))
            restartIfNotTheLast();
    }

    private static void restartIfNotTheLast() {
        channel = ((TextChannel) Init.devarea.getChannelById(Init.idBump).block());
        if (!channel.getLastMessageId().get().equals(message.getId())) {
            replace(msg -> msg.setEmbed(embedCreateSpec -> {
                embedCreateSpec.setColor(message.getEmbeds().get(0).getColor().get());
                embedCreateSpec.setDescription(message.getEmbeds().get(0).getDescription().get());
            }));
        }
    }
}
