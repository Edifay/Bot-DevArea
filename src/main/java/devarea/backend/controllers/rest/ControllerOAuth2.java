package devarea.backend.controllers.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.backend.controllers.data.UserInfo;
import devarea.bot.Init;
import discord4j.common.util.Snowflake;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static devarea.bot.event.FunctionEvent.startAway;

@CrossOrigin()
@RestController
public class ControllerOAuth2 {

    private static final ControllerFonction.PasswordGenerator passwordGenerator;

    static {
        passwordGenerator = new ControllerFonction.PasswordGenerator(new ControllerFonction.PasswordGenerator.PasswordGeneratorBuilder()
                .useDigits(true)
                .useLower(true)
                .useUpper(true)
                .usePunctuation(false)
        );
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final HashMap<String, UserInfo> userInfo_cache = new HashMap<>();

    public static void init() {
        load();
        verifyAllMember();
    }

    @GetMapping("/auth/get")
    public UserInfo get(@RequestParam(value = "code") final String code, @RequestParam(value = "force", required = false, defaultValue = "false") final String force) throws IOException {
        if (userInfo_cache.containsKey(code))
            userInfo_cache.get(code).verifFetchNeeded(Boolean.parseBoolean(force));
        else
            return null;

        return userInfo_cache.get(code);
    }

    @GetMapping("/auth/remove")
    public boolean remove(@RequestParam(value = "code") final String code) throws IOException {

        if (userInfo_cache.containsKey(code)) {
            userInfo_cache.remove(code);
            startAway(ControllerOAuth2::save);
            return true;
        }

        return false;
    }

    public static void save() {
        try {

            HashMap<String, String> code_link_id = new HashMap<>();
            for (Map.Entry<String, UserInfo> random : userInfo_cache.entrySet())
                code_link_id.put(random.getKey(), random.getValue().getId());

            mapper.writeValue(new File("auth.json"), code_link_id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        try {
            File file = new File("auth.json");
            if (!file.exists())
                save();

            HashMap<String, String> code_link_id = mapper.readValue(file, new TypeReference<>() {
            });

            for (Map.Entry<String, String> random : code_link_id.entrySet())
                userInfo_cache.put(random.getKey(), new UserInfo(random.getValue()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean isMember(final String member_id) {
        return Init.membersId.contains(Snowflake.of(member_id));
    }

    public static boolean isAlreadyBind(final String member_id) {
        for (Map.Entry<String, UserInfo> random : userInfo_cache.entrySet())
            if (random.getValue().getId().equals(member_id))
                return true;
        return false;
    }

    /*
        Return AuthCode from member_id
     */
    public static String getUserLink(final String member_id) {
        for (Map.Entry<String, UserInfo> random : userInfo_cache.entrySet())
            if (random.getValue().getId().equals(member_id))
                return random.getKey();
        return null;
    }

    public static void left(final String member_id) {
        if (isAlreadyBind(member_id))
            userInfo_cache.remove(getUserLink(member_id));
    }

    public static UserInfo getInfoFor(final String code) {
        return userInfo_cache.get(code);
    }

    public static String getAuth(final String memberID) {
        if (isAlreadyBind(memberID))
            return getUserLink(memberID);
        return addNewAuthForUser(memberID);
    }

    public static String addNewAuthForUser(final String memberID) {
        final String code = passwordGenerator.generate(30);
        userInfo_cache.put(code, new UserInfo(memberID));
        startAway(ControllerOAuth2::save);
        return code;
    }

    public static void verifyAllMember() {
        ArrayList<String> at_remove = new ArrayList<>();
        for (Map.Entry<String, UserInfo> random : userInfo_cache.entrySet())
            if (!isMember(random.getValue().getId()))
                at_remove.add(random.getKey());

        for (String key : at_remove)
            userInfo_cache.remove(key);
    }

}
