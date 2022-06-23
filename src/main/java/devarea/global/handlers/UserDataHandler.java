package devarea.global.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.global.handlers.handlerData.UserData;

import java.io.File;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static devarea.bot.event.FunctionEvent.startAway;

public class UserDataHandler {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static HashMap<String, UserData> data = new HashMap<>();

    public static void init() {
        load();
    }

    public static void load() {
        File file = new File("userData.json");
        if (!file.exists()) save();
        try {
            data = mapper.readValue(file, new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            mapper.writeValue(new File("userData.json"), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static UserData get(final String userID) {
        UserData currentData = data.get(userID);
        if (currentData == null)
            currentData = add(userID);
        return currentData;
    }

    private static UserData add(final String userID) {
        UserData currentData = new UserData();
        data.put(userID, currentData);
        startAway(UserDataHandler::save);
        return currentData;
    }

    public static void updated() {
        save();
    }

    public static void addOneToXpGainHistory(final String userID) {
        addOneToXpGainHistory(userID, 1);
    }

    public static void addOneToXpGainHistory(final String userID, Integer value) {
        UserData userData = get(userID);

        String toDay = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH).format(LocalDateTime.now());

        if (userData.xpGainHistory.containsKey(toDay))
            userData.xpGainHistory.put(toDay, userData.xpGainHistory.get(toDay) + value);
        else
            userData.xpGainHistory.put(toDay, value);

        updated();
    }
}
