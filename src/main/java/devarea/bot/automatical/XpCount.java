package devarea.bot.automatical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.backend.controllers.data.XpMember;
import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class XpCount {

    private static LinkedHashMap<Snowflake, Integer> xp = new LinkedHashMap<>();

    public static HashMap<String, Integer> xpLeft = new HashMap<>();

    private static final ArrayList<Snowflake> already = new ArrayList<>();

    public static LinkedHashMap<Snowflake, Integer> sortByValue(final Map<Snowflake, Integer> wordCounts) {
        return wordCounts.entrySet()
                .stream()
                .sorted((Map.Entry.<Snowflake, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static void init() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("./xp.json");
        if (!file.exists())
            save();
        try {
            LinkedHashMap<String, Integer> obj = mapper.readValue(file, new TypeReference<>() {
            });
            obj.forEach((s, aLong) -> xp.put(Snowflake.of(s), aLong));
            xp = sortByValue(xp);
            loadLeft();
            verifLeft();
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
            final Map<String, Integer> stock = new LinkedHashMap<>();
            xp.forEach((snowflake, integer) -> stock.put(snowflake.asString(), integer));
            mapper.writeValue(new File("./xp.json"), stock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveLeft() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            final Map<String, Integer> stock = new LinkedHashMap<>();
            xpLeft.forEach(stock::put);
            mapper.writeValue(new File("./xpLeft.json"), stock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadLeft() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("./xpLeft.json");
        if (!file.exists())
            saveLeft();
        try {
            HashMap<String, Integer> obj = mapper.readValue(file, new TypeReference<>() {
            });
            obj.forEach((s, aLong) -> xpLeft.put(s, aLong));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void onMessage(MessageCreateEvent event) {
        Member member = event.getMember().get();
        if (!already.contains(member.getId())) {
            if (xp.containsKey(member.getId())) {
                if (XpCount.getLevelForXp(xp.get(member.getId())) < XpCount.getLevelForXp(xp.get(member.getId()) + 1)) {
                    Command.send((TextChannel) Init.devarea.getChannelById(Init.idCommands).block(), messageCreateSpec -> {
                        messageCreateSpec.setContent("<@" + member.getId().asString() + ">");
                        messageCreateSpec.setEmbed(embedCreateSpec -> {
                            embedCreateSpec.setDescription("Bien joué <@" + member.getId().asString() + ">, tu es passé niveau " + XpCount.getLevelForXp(xp.get(member.getId()) + 1) + " !");
                            embedCreateSpec.setTimestamp(Instant.now());
                            embedCreateSpec.setColor(ColorsUsed.same);
                        });
                    }, false);
                }
                xp.put(member.getId(), xp.get(member.getId()) + 1);
                xp = sortByValue(xp);
            } else {
                xp.put(member.getId(), 1);
            }
            already.add(member.getId());
            new Thread(() -> {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                }
                already.remove(member.getId());
            }).start();
        }
    }

    public synchronized static Integer getXpOf(Snowflake id) {
        return xp.get(id);
    }

    public synchronized static Integer getRankOf(Snowflake id) {
        int i = 0;
        for (Snowflake randomId : xp.keySet())
            if (randomId.equals(id))
                return ++i;
            else
                i++;
        return i;
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

    public synchronized static XpMember[] getListOfIndex(final int start, int end) {
        if (start > xp.size())
            return new XpMember[0];
        if (end > xp.size())
            end = xp.size();

        XpMember[] atReturn = new XpMember[end - start];
        int setted = 0;
        int i = 0;
        for (Map.Entry<Snowflake, Integer> random : xp.entrySet()) {
            if (i >= start && i < end) {
                atReturn[setted] = new XpMember(random.getKey().asString(), random.getValue(), i);
                setted++;
            }
            i++;
        }
        return atReturn;
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

    public synchronized static void verifLeft() {
        List<Member> members =
                Init.devarea
                        .getMembers().buffer().blockLast();
        ArrayList<Snowflake> memberIds = new ArrayList<>();
        for (Member member : members)
            memberIds.add(member.getId());

        synchronized (XpCount.class) {
            ArrayList<Map.Entry<Snowflake, Integer>> atRemove = new ArrayList<>();
            for (Map.Entry<Snowflake, Integer> random : xp.entrySet()) {
                if (!memberIds.contains(random.getKey())) {
                    atRemove.add(random);
                }
            }
            for (Map.Entry<Snowflake, Integer> random : atRemove) {
                xp.remove(random.getKey());
                xpLeft.put(random.getKey().asString(), random.getValue());
            }

            save();
            saveLeft();
        }
    }

    public static synchronized void addNewMember(Snowflake id) {
        if (xpLeft.containsKey(id.asString())) {
            xp.put(id, xpLeft.get(id.asString()));
            xp = sortByValue(xp);
            xpLeft.remove(id.asString());
            saveLeft();
        }
    }


    public static synchronized void remove(Snowflake id) {
        if (xp.containsKey(id))
            xpLeft.put(id.asString(), xp.get(id));
        saveLeft();
        xp.remove(id);
    }

}
