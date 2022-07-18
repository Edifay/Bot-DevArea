package devarea.global.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.bot.commands.commandTools.Mission;
import devarea.global.cache.MemberCache;
import devarea.global.handlers.handlerData.UserData;
import discord4j.common.util.Snowflake;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static devarea.bot.event.FunctionEvent.repeatEachMillis;
import static devarea.bot.event.FunctionEvent.startAway;

public class UserDataHandler {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static HashMap<String, UserData> data = new HashMap<>();

    /*
        Initialise UserDataHandler
     */
    public static void init() {
        load();
        verifyEachMemberPresence();

        // Auto Save Each 5min
        repeatEachMillis(UserDataHandler::updated, 300000, false);
    }

    /*
        Load data
     */
    private static void load() {
        File file = new File("userData.json");
        if (!file.exists()) save();
        try {
            data = mapper.readValue(file, new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        Save data
     */
    private static void save() {
        try {
            System.out.println("Saving !");
            mapper.writeValue(new File("userData.json"), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        Get UserData from member -> userID
     */
    public static UserData get(final String userID) {
        UserData currentData = data.get(userID);
        if (currentData == null)
            currentData = add(userID);
        return currentData;
    }

    /*
        Add UserData for member -> userID
     */
    private static UserData add(final String userID) {
        UserData currentData = new UserData();
        data.put(userID, currentData);
        startAway(UserDataHandler::updated);
        return currentData;
    }

    /*
        Variant of left(String, boolean)
     */
    public static void left(String userID) {
        left(userID, true);
    }

    /*
        Remove data from member who left, and call modification to others modules
     */
    public static void left(String userID, boolean update) {
        XPHandler.removeMember(Snowflake.of(userID));
        MissionsHandler.left(userID, get(userID).missions.values());

        data.remove(userID);
        if (update)
            updated();
    }

    /*
        Verify the presence of all UserData members. We don't keep data of member who left !
     */
    private static void verifyEachMemberPresence() {
        ArrayList<String> dataAtRemove = new ArrayList<>();
        for (Map.Entry<String, UserData> userData : data.entrySet())
            if (!MemberCache.contain(userData.getKey())) {
                System.out.println("Member Presence verification problem found : " + userData.getKey() + " is not " +
                        "here anymore !");
                dataAtRemove.add(userData.getKey());
            }

        for (String memberID : dataAtRemove)
            left(memberID, false);

        if (dataAtRemove.size() != 0)
            updated();

    }

    /*
        Force update, bypass auto save
     */
    public static void updated() {
        save();
    }

    /*
        Add one xp to XP Gain History in UserData
     */
    public static void addOneToXpGainHistory(final String userID, Integer value) {
        UserData userData = get(userID);

        String toDay = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH).format(LocalDateTime.now());

        if (userData.xpGainHistory.containsKey(toDay))
            userData.xpGainHistory.put(toDay, userData.xpGainHistory.get(toDay) + value);
        else
            userData.xpGainHistory.put(toDay, value);

    }

    /*
        Get an XP list of all members who have xp !
     */
    public static LinkedHashMap<Snowflake, Integer> getXpList() {
        LinkedHashMap<Snowflake, Integer> map = new LinkedHashMap<>();
        for (Map.Entry<String, UserData> currentData : data.entrySet())
            if (currentData.getValue().xp != null)
                map.put(Snowflake.of(currentData.getKey()), currentData.getValue().xp);
        return map;
    }

    /*
        Inject an Xp List on all members !
     */
    public static void setXpList(Map<Snowflake, Integer> map) {
        map.forEach((id, xp) -> get(id.asString()).xp = xp);
    }

    public static void setMissionList(Map<String, Mission> map) {
        for (UserData current : data.values())
            if (current.missions.size() != 0)
                current.missions = new LinkedHashMap<>();

        for (Map.Entry<String, Mission> entry : map.entrySet())
            get(entry.getValue().getMemberId()).missions.put(entry.getKey(), entry.getValue());

        updated();
    }
    /*
        Return the list of all missions from members
     */
    public static ArrayList<Mission> getMissionList() {
        ArrayList<Mission> missions = new ArrayList<>();
        for (UserData current : data.values())
            if (current.missions.size() != 0)
                missions.addAll(current.missions.values());
        return missions;
    }

}
