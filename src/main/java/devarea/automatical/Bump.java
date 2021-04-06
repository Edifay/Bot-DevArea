package devarea.automatical;

import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.Data.ColorsUsed;
import devarea.Main;
import devarea.commands.Command;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Bump {

    private static long dateToBump;
    private static Message message;
    private static TextChannel channel;

    private static Snowflake last;
    private static HashMap<Snowflake, Integer> bumps = new HashMap<>();

    public static void save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            HashMap<String, Integer> obj = new HashMap<>();
            bumps.forEach((s, i) -> obj.put(s.asString(), i));
            mapper.writeValue(new File("./bumps.json"), obj);
        } catch (IOException e) {
            save();
            e.printStackTrace();
        }
    }

    public static void read() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            HashMap<String, Integer> obj = mapper.readValue(new File("./bumps.json"), HashMap.class);
            obj.forEach((s, integer) -> bumps.put(Snowflake.of(s), integer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void init() {
        read();

        channel = (TextChannel) Main.devarea.getChannelById(Main.idBump).block();
        message = Command.sendEmbed(channel, embedCreateSpec -> {
            embedCreateSpec.setColor(ColorsUsed.wrong);
            embedCreateSpec.setDescription("Le bot vien de s'initialisé utilisez la commande `!d bump`, pour lancer le compte à rebourd.");
        });
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
                        } else if (!message.getEmbeds().get(0).getDescription().get().equals("Le bump est disponible avec la commande `!d bump`."))
                            replace(msg -> msg.setEmbed(embed -> {
                                embed.setDescription("Le bump est disponible avec la commande `!d bump`.");
                                embed.setColor(ColorsUsed.same);
                            }));
                    } catch (Exception ignored) {
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }).start();

    }

    public static void bumpEffectued() {
        if (bumps.containsKey(last))
            bumps.put(last, bumps.get(last) + 1);
        else
            bumps.put(last, 1);
    }

    public synchronized static void getDisboardMessage(MessageCreateEvent event) {
        if (!event.getMessage().getChannel().block().getId().equals(channel.getId()))
            return;

        String[] coupes = event.getMessage().getEmbeds().get(0).getDescription().get().split(" ");
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
            bumpEffectued();
            save();
        }
    }

    private synchronized static void replace(final Consumer<? super MessageCreateSpec> spec) {
        Command.delete(message);
        message = Command.send(channel, spec);
    }

    private synchronized static void edit(final Consumer<? super MessageEditSpec> spec) {
        message = message.edit(spec).block();
    }

    public static void messageInChannel(MessageCreateEvent event) {
        if (!event.getMessage().getContent().equalsIgnoreCase("!d bump"))
            restartIfNotTheLast();
        else
            last = event.getMember().get().getId();
    }

    private static void restartIfNotTheLast() {
        channel = ((TextChannel) Main.devarea.getChannelById(Main.idBump).block());
        if (!channel.getLastMessageId().get().equals(message.getId())) {
            replace(msg -> msg.setEmbed(embedCreateSpec -> {
                embedCreateSpec.setColor(message.getEmbeds().get(0).getColor().get());
                embedCreateSpec.setDescription(message.getEmbeds().get(0).getDescription().get());
            }));
        }
    }

    public synchronized static Integer getBumpsOf(Snowflake id) {
        return bumps.get(id);
    }

    public synchronized static Integer getRankOf(Snowflake id) {
        Integer[] sorted = bumps.values().toArray(new Integer[0]);
        Arrays.sort(sorted);
        for (int i = 0; i < sorted.length; i++) {
            if (sorted[i].equals(bumps.get(id)))
                return sorted.length - i;
        }
        return -1;
    }

    public synchronized static Snowflake[] getSortedMemberArray() {
        Integer[] sorted = bumps.values().toArray(new Integer[0]);
        Arrays.sort(sorted, Collections.reverseOrder());
        Snowflake[] array = new Snowflake[sorted.length];
        for (int i = 0; i < sorted.length; i++) {
            for (Map.Entry<Snowflake, Integer> entry : bumps.entrySet()) {
                Snowflake snowflake = entry.getKey();
                Integer integer = entry.getValue();
                if (integer.equals(sorted[i])) {
                    array[i] = snowflake;
                    break;
                }
            }
        }
        return array;
    }


}
