package devarea.global.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.backend.controllers.tools.WebXPMember;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
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

import static devarea.global.utils.ThreadHandler.*;

public class XPHandler {

    private static LinkedHashMap<Snowflake, Integer> xp = new LinkedHashMap<>();

    public static HashMap<String, Integer> xpLeft = new HashMap<>();

    private static final ArrayList<Snowflake> memberInCouldDown = new ArrayList<>();

    public static HashMap<Snowflake, Integer> currentXpEarnVoiceStatus = new HashMap<>();

    /*
        Initialise the XP System
     */
    public static void init() {
        loadXp();
        loadXpLeft();

        repeatEachMillis(XPHandler::saveXp, 600000, true);

        setupVoiceXpEarn();
    }

    /*
        Setup the loop to add xp to member current in a voice channel !
     */
    private static void setupVoiceXpEarn() {
        repeatEachMillis(() -> {
            List<VoiceState> states = Init.devarea.getVoiceStates().buffer().blockLast();
            if (states != null)

                for (VoiceState voice : states) {
                    Member member = MemberCache.get(voice.getUserId().asString());
                    boolean memberEarnXpVoiceStatus = currentXpEarnVoiceStatus.containsKey(member.getId());

                    if (!memberEarnXpVoiceStatus || currentXpEarnVoiceStatus.get(member.getId()) <= 90) {
                        // Adding xp and apply couldown and maxXpEarnInvVoice count
                        addXpToMember(member, false);
                        currentXpEarnVoiceStatus.put(member.getId(), memberEarnXpVoiceStatus ?
                                currentXpEarnVoiceStatus.get(member.getId()) + 1 : 1);

                        // Removing 1xp earn after 24h
                        startAwayIn(() -> currentXpEarnVoiceStatus.put(member.getId(),
                                currentXpEarnVoiceStatus.get(member.getId()) - 1), 86400000, false);
                    }
                }

        }, 60000, true);
    }

    private static synchronized void saveXp() {
        UserDataHandler.setXpList(xp);
    }

    private static void loadXp() {
        xp = sortMap(UserDataHandler.getXpList());
    }

    public static void loadXpLeft() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("./xpLeft.json");
        if (!file.exists()) saveXpLeft();
        try {
            HashMap<String, Integer> obj = mapper.readValue(file, new TypeReference<>() {
            });
            obj.forEach((s, aLong) -> xpLeft.put(s, aLong));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void saveXpLeft() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            final Map<String, Integer> stock = new LinkedHashMap<>();
            xpLeft.forEach(stock::put);
            mapper.writeValue(new File("./xpLeft.json"), stock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static LinkedHashMap<Snowflake, Integer> sortMap(final Map<Snowflake, Integer> wordCounts) {
        return wordCounts.entrySet().stream().sorted((Map.Entry.<Snowflake, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    /*
       Sort the list of xp
     */
    public static void sortXp() {
        xp = sortMap(xp);
    }

    /*
        Default values :
        withTimer -> true
        xp earn value -> 1xp
     */
    public synchronized static void addXpToMember(Member member) {
        addXpToMember(member, true);
    }

    /*
        Default values :
        xp earn value -> 1xp
     */
    public synchronized static void addXpToMember(Member member, boolean withTimer) {
        addXpToMember(member, withTimer, 1);
    }

    public synchronized static void addXpToMember(Member member, boolean withTimer, Integer value) {
        if (!withTimer || !memberInCouldDown.contains(member.getId())) {

            if (xp.containsKey(member.getId())) {
                verifyIfNextLevelReach(member, value);
                xp.put(member.getId(), xp.get(member.getId()) + value);
                sortXp();
            } else if (!addNewMember(member.getId()))
                xp.put(member.getId(), value);

            UserDataHandler.addOneToXpGainHistory(member.getId().asString(), value);

            // setup couldown xp earn
            if (withTimer) {
                memberInCouldDown.add(member.getId());
                startAwayIn(() -> removeSafely(member), 6000, false);
            }
        }
    }

    private static void verifyIfNextLevelReach(Member member, Integer value) {
        if (XPHandler.getLevelForXp(xp.get(member.getId())) < XPHandler.getLevelForXp(xp.get(member.getId()) + value))
            startAway(() -> {
                Command.send((TextChannel) ChannelCache.watch(Init.initial.command_channel.asString()),
                        MessageCreateSpec.builder().content("<@" + member.getId().asString() + ">").addEmbed(EmbedCreateSpec.builder().description("Bien joué <@" + member.getId().asString() + ">, tu es passé niveau " + XPHandler.getLevelForXp(xp.get(member.getId()) + 1) + " !").timestamp(Instant.now()).color(ColorsUsed.same).build()).build(), false);
            });
    }

    /*
        Remove the amount of xp given to the member
     */
    public synchronized static void removeXpToMember(Member member, Integer value) {

        final Snowflake memberId = member.getId();

        if (xp.containsKey(memberId)) {
            final Integer memberXp = getXpOfMember(memberId);
            xp.replace(memberId, memberXp, memberXp - value);
        }
    }

    /*
        Get the xp count of the id member requested
     */
    public synchronized static Integer getXpOfMember(Snowflake id) {
        return xp.get(id);
    }

    /*
        Get the rank of the id member requested
     */
    public synchronized static Integer getRankOfMember(Snowflake id) {
        int i = 0;
        for (Snowflake randomId : xp.keySet())
            if (randomId.equals(id)) return ++i;
            else i++;
        return i;
    }

    /*
        Get a sorted array from the xp ArrayList
     */
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

    /*
        Return the list of WebXPMember
     */
    public synchronized static WebXPMember[] getWebXPMemberListOfIndex(final int start, int end) {
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

    /*
        Get the level of an xp amount
     */
    public synchronized static int getLevelForXp(int xp) {
        int level = 0;
        while (xp >= getAmountForLevel(level)) level++;
        return --level;
    }

    /*
        Get the xp amount for a level
     */
    public static int getAmountForLevel(int level) {
        return (int) (3 * (Math.pow(level, 2)));
    }

    /*
        Return if the memberID have already speak on the server
     */
    public static boolean haveBeenSet(Snowflake id) {
        return xp.containsKey(id);
    }

    /*
        Check if the member have an old xp.
        Return :
        true : if old xp could be fetched
        false : if old xp couldn't be fetched
     */
    public static synchronized boolean addNewMember(Snowflake id) {
        if (xpLeft.containsKey(id.asString())) {
            xp.put(id, xpLeft.get(id.asString()));
            sortXp();
            xpLeft.remove(id.asString());
            saveXpLeft();
            return true;
        }
        return false;
    }

    /*
        Remove member to actual XP and transfer his xp data to XpLeft
     */
    public static synchronized void removeMember(Snowflake id) {
        if (xp.containsKey(id)) {
            xpLeft.put(id.asString(), xp.get(id));
            saveXpLeft();
            xp.remove(id);
        }
    }
    /*
        Secure the stop of the bot
     */
    public static synchronized void stop() {
        saveXp();
    }

    public static synchronized void removeSafely(Member member) {
        memberInCouldDown.remove(member.getId());
    }

}
