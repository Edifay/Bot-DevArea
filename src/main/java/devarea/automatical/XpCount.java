package devarea.automatical;

import devarea.Main;
import devarea.Data.ColorsUsed;
import devarea.commands.Command;
import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class XpCount {

    private static HashMap<Snowflake, Integer> xp = new HashMap<>();

    private static ArrayList<Snowflake> alreay = new ArrayList<>();

    public static void init() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            HashMap<String, Integer> obj = mapper.readValue(new File("./xp.json"), HashMap.class);
            obj.forEach((s, aLong) -> xp.put(Snowflake.of(s), aLong));
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(60000);
                    save();
                }
            } catch (Exception e) {
            }
        }).start();
    }

    public static void save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            final HashMap<String, Integer> stock = new HashMap<>();
            xp.forEach((snowflake, integer) -> stock.put(snowflake.asString(), integer));
            mapper.writeValue(new File("./xp.json"), stock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized static void onMessage(MessageCreateEvent event) {
        Member member = event.getMember().get();
        if (!alreay.contains(member.getId())) {
            if (xp.containsKey(member.getId())) {
                if (XpCount.getLevelForXp(xp.get(member.getId())) < XpCount.getLevelForXp(xp.get(member.getId()) + 1)) {
                    Command.send((TextChannel) Main.devarea.getChannelById(Main.idCommands).block(), messageCreateSpec -> {
                        messageCreateSpec.setContent("<@" + member.getId().asString() + ">");
                        messageCreateSpec.setEmbed(embedCreateSpec -> {
                            embedCreateSpec.setDescription("Bien joué <@" + member.getId().asString() + ">, tu es passé niveau " + XpCount.getLevelForXp(xp.get(member.getId()) + 1) + " !");
                            embedCreateSpec.setTimestamp(Instant.now());
                            embedCreateSpec.setColor(ColorsUsed.same);
                        });
                    });
                }
                xp.put(member.getId(), xp.get(member.getId()) + 1);
            } else {
                xp.put(member.getId(), 1);
            }
            alreay.add(member.getId());
            new Thread(() -> {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                }
                alreay.remove(member.getId());
            }).start();
        }
    }

    public synchronized static Integer getXpOf(Snowflake id) {
        return xp.get(id);
    }

    public synchronized static Integer getRankOf(Snowflake id) {
        Integer[] sorted = xp.values().toArray(new Integer[0]);
        Arrays.sort(sorted);
        for (int i = 0; i < sorted.length; i++) {
            if (sorted[i].equals(xp.get(id)))
                return sorted.length - i;
        }
        return -1;
    }

    public synchronized static Snowflake[] getSortedMemberArray() {
        Integer[] sorted = xp.values().toArray(new Integer[0]);
        Arrays.sort(sorted, Collections.reverseOrder());
        Snowflake[] array = new Snowflake[sorted.length];
        for (int i = 0; i < sorted.length; i++) {
            for (Map.Entry<Snowflake, Integer> entry : xp.entrySet()) {
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

    public synchronized static int getLevelForXp(int xp) {
        int level = 0;
        while (xp >= getAmountForLevel(level))
            level++;
        return --level;
    }

    public static int getAmountForLevel(int level) {
        return (int) (3 * (Math.pow(level, 2)));
    }

    public static boolean haveBeenSet(Snowflake id) {
        return xp.containsKey(id);
    }

}
