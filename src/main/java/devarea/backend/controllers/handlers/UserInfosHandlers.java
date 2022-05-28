package devarea.backend.controllers.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.backend.controllers.handlers.tools.UserData;

import java.io.File;
import java.util.HashMap;

import static devarea.bot.event.FunctionEvent.startAway;

public class UserInfosHandlers {

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

    public static UserData get(String userID) {
        UserData currentData = data.get(userID);
        if (currentData == null)
            currentData = add(userID);
        return currentData;
    }

    private static UserData add(String userID) {
        UserData currentData = new UserData();
        data.put(userID, currentData);
        startAway(UserInfosHandlers::save);
        return currentData;
    }

    public static void updated() {
        save();
    }
}
