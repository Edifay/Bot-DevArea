package devarea.bot.automatical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.backend.controllers.tools.WebXPMember;
import devarea.bot.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static devarea.bot.event.FunctionEvent.startAway;

public class XPHandler {

    private static LinkedHashMap<Snowflake, Integer> xp = new LinkedHashMap<>();

    public static HashMap<String, Integer> xpLeft = new HashMap<>();

    private static final ArrayList<Snowflake> already = new ArrayList<>();

    public static LinkedHashMap<Snowflake, Integer> sortByValue(final Map<Snowflake, Integer> wordCounts) {
        return wordCounts.entrySet().stream().sorted((Map.Entry.<Snowflake, Integer>comparingByValue().reversed())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static void init() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("./xp.json");
        if (!file.exists()) save();
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
                    verifLeft();
                    save();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(60000);
                    List<VoiceState> states = Init.devarea.getVoiceStates().buffer().blockLast();
                    for (VoiceState voice : states)
                        addMember(voice.getMember().block(), false);
                }
            } catch (Exception e) {

            }
        }).start();
    }

    public static synchronized void save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            final Map<String, Integer> stock = new LinkedHashMap<>();
            xp.forEach((snowflake, integer) -> stock.put(snowflake.asString(), integer));
            mapper.writeValue(new File("./xp.json"), stock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void saveLeft() {
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
        if (!file.exists()) saveLeft();
        try {
            HashMap<String, Integer> obj = mapper.readValue(file, new TypeReference<>() {
            });
            obj.forEach((s, aLong) -> xpLeft.put(s, aLong));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void onMessage(MessageCreateEvent event) {
        addMember(event.getMember().get());
    }

    public synchronized static void addMember(Member member) {
        addMember(member, true);
    }

    public synchronized static void addMember(Member member, boolean withTimer) {
        if (!withTimer || !already.contains(member.getId())) {
            if (xp.containsKey(member.getId())) {
                if (XPHandler.getLevelForXp(xp.get(member.getId())) < XPHandler.getLevelForXp(xp.get(member.getId()) + 1))
                    startAway(() -> {
                        Command.send((TextChannel) Init.devarea.getChannelById(Init.idCommands).block(),
                                MessageCreateSpec.builder().content("<@" + member.getId().asString() + ">").addEmbed(EmbedCreateSpec.builder().description("Bien joué <@" + member.getId().asString() + ">, tu es passé niveau " + XPHandler.getLevelForXp(xp.get(member.getId()) + 1) + " !").timestamp(Instant.now()).color(ColorsUsed.same).build()).build(), false);
                    });
                xp.put(member.getId(), xp.get(member.getId()) + 1);
                xp = sortByValue(xp);
            } else
                xp.put(member.getId(), 1);

            if (withTimer) {
                already.add(member.getId());
                new Thread(() -> {
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                    }
                    removeSafely(member);
                }).start();
            }
        }
    }

    public synchronized static void addXp(Member member, Integer value) {

        final Snowflake memberId = member.getId();

        if (xp.containsKey(memberId)) {
            final Integer memberXp = getXpOf(memberId);
            xp.put(memberId, memberXp + value);
        }
    }

    public synchronized static void removeXp(Member member, Integer value) {

        final Snowflake memberId = member.getId();

        if (xp.containsKey(memberId)) {
            final Integer memberXp = getXpOf(memberId);
            xp.replace(memberId, memberXp, memberXp - value);
        }
    }

    public synchronized static Integer getXpOf(Snowflake id) {
        return xp.get(id);
    }

    public synchronized static Integer getRankOf(Snowflake id) {
        int i = 0;
        for (Snowflake randomId : xp.keySet())
            if (randomId.equals(id)) return ++i;
            else i++;
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

    public synchronized static WebXPMember[] getListOfIndex(final int start, int end) {
        if (start > xp.size()) return new WebXPMember[0];
        if (end > xp.size()) end = xp.size();

        WebXPMember[] atReturn = new WebXPMember[end - start];
        int setted = 0;
        int i = 0;
        for (Map.Entry<Snowflake, Integer> random : xp.entrySet()) {
            if (i >= start && i < end) {
                atReturn[setted] = new WebXPMember(random.getKey().asString(), random.getValue(), i);
                setted++;
            }
            i++;
        }
        return atReturn;
    }

    public synchronized static int getLevelForXp(int xp) {
        int level = 0;
        while (xp >= getAmountForLevel(level)) level++;
        return --level;
    }

    public static int getAmountForLevel(int level) {
        return (int) (3 * (Math.pow(level, 2)));
    }

    public static boolean haveBeenSet(Snowflake id) {
        return xp.containsKey(id);
    }

    public synchronized static void verifLeft() {
        ArrayList<Snowflake> memberIds = new ArrayList<>();
        MemberCache.cache().forEach((k, v) -> memberIds.add(Snowflake.of(k)));

        synchronized (XPHandler.class) {
            ArrayList<Map.Entry<Snowflake, Integer>> atRemove = new ArrayList<>();

            for (Map.Entry<Snowflake, Integer> random : xp.entrySet())
                if (!memberIds.contains(random.getKey()))
                    atRemove.add(random);

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
        if (xp.containsKey(id)) xpLeft.put(id.asString(), xp.get(id));
        saveLeft();
        xp.remove(id);
    }

    public static synchronized void stop() {
        save();
    }

    public static synchronized void removeSafely(Member member) {
        already.remove(member.getId());
    }

}
