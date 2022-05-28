package devarea.backend.controllers.rest.requestContent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.backend.controllers.tools.userInfos.WebPrivateUserInfos;
import devarea.bot.cache.MemberCache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static devarea.bot.event.FunctionEvent.startAway;

public class RequestHandlerAuth {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HashMap<String, String> codeToMemberID = new HashMap<>();

    private static final RequestHandlerGlobal.PasswordGenerator passwordGenerator;

    static {
        passwordGenerator =
                new RequestHandlerGlobal.PasswordGenerator(new RequestHandlerGlobal.PasswordGenerator.PasswordGeneratorBuilder()
                        .useDigits(true)
                        .useLower(true)
                        .useUpper(true)
                        .usePunctuation(false)
                );
    }

    public static void init() {
        load();
        verifyAllMember();
    }

    public static WebPrivateUserInfos get(final String code) {
        if (containCode(code))
            return (WebPrivateUserInfos) new WebPrivateUserInfos(codeToMemberID.get(code)).update();
        return null;
    }

    public static boolean deleteBinding(final String code) {
        boolean isDeleted = codeToMemberID.remove(code) != null;
        startAway(RequestHandlerAuth::save);
        return isDeleted;
    }

    public static boolean containCode(final String code) {
        return codeToMemberID.containsKey(code);
    }

    public static boolean containMember(final String memberID) {
        return codeToMemberID.containsValue(memberID);
    }

    public static String getCodeForMember(final String memberID) {
        for (Map.Entry<String, String> random : codeToMemberID.entrySet())
            if (random.getValue().equals(memberID))
                return random.getKey();

        return addNewAuthForUser(memberID);
    }

    private static String addNewAuthForUser(final String memberID) {
        final String code = passwordGenerator.generate(30);
        codeToMemberID.put(code, memberID);
        startAway(RequestHandlerAuth::save);
        return code;
    }

    private static void save() {
        try {
            mapper.writeValue(new File("auth.json"), codeToMemberID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void load() {
        try {
            File file = new File("auth.json");
            if (!file.exists())
                save();

            HashMap<String, String> code_link_id = mapper.readValue(file, new TypeReference<>() {
            });

            codeToMemberID.putAll(code_link_id);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void verifyAllMember() {
        ArrayList<String> at_remove = new ArrayList<>();
        for (Map.Entry<String, String> random : codeToMemberID.entrySet())
            if (!MemberCache.contain(random.getValue()))
                at_remove.add(random.getKey());

        for (String key : at_remove)
            codeToMemberID.remove(key);
    }

    public static void left(final String memberID) {
        RequestHandlerAuth.deleteBinding(RequestHandlerAuth.getCodeForMember(memberID));
    }

    public static WebPrivateUserInfos requestGetUserInfo(final String code) {
        return get(code);
    }

    public static boolean requestDeleteAccount(final String code) {
        return deleteBinding(code);
    }


}
