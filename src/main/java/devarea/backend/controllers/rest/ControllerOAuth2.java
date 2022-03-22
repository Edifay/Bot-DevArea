package devarea.backend.controllers.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dependencies.auth.main.OAuthBuilder;
import dependencies.auth.main.Response;
import devarea.backend.controllers.data.UserInfo;
import devarea.bot.Init;
import discord4j.common.util.Snowflake;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static devarea.bot.event.FunctionEvent.startAway;

@CrossOrigin()
@RestController
public class ControllerOAuth2 {

    private static final String
            client_id = "579257697048985601";
    private static String client_secret;

    static {
        try {
            client_secret = new Scanner(new File("./OAuth2_secret.secret")).nextLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final String redirect_url = "https://devarea.fr/data/auth";

    private static final ObjectMapper mapper = new ObjectMapper();

    private static HashMap<String, UserInfo> userInfo_cache = new HashMap<>();

    public static void init() {
        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/auth/get")
    public UserInfo get(@RequestParam(value = "code") final String code, @RequestParam(value = "force", required = false, defaultValue = "false") final String force) throws IOException {
        System.out.println("Get !");
        if (userInfo_cache.containsKey(code))
            userInfo_cache.get(code).verifFetchNeeded(Boolean.parseBoolean(force));
        else
            return null;

        return userInfo_cache.get(code);
    }

    @GetMapping("/auth/remove")
    public boolean remove(@RequestParam(value = "code") final String code) throws IOException {
        if (userInfo_cache.containsKey(code)) {
            if (userInfo_cache.get(code).getBuilder() != null)
                userInfo_cache.get(code).getBuilder().revoke();
            userInfo_cache.remove(code);
            startAway(() -> {
                try {
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        return false;
    }

    @GetMapping("/auth")
    public String auth(@RequestParam(value = "code") final String code) {
        if (!userInfo_cache.containsKey(code)) {

            OAuthBuilder builder = new OAuthBuilder(client_id, client_secret).setScopes(new String[]{"identify"}).setRedirectURI(redirect_url);

            if (Response.ERROR == builder.exchange(code))
                return "ERROR IN LOGIN TO DISCORD";

            if (isAlreadyBind(builder.getIdUser())) {
                UserInfo userInfo = userInfo_cache.get(getUserLink(builder.getIdUser()));
                if (userInfo.getBuilder() == null) {
                    builder.enableAutoRefresh();
                    userInfo.setBuilder(builder);
                    userInfo.verifFetchNeeded(false);
                } else {
                    builder.revoke();
                }
                return "<meta http-equiv=\"refresh\" content=\"0; url=https://devarea.fr/?code=" + getUserLink(builder.getIdUser()) + "\" />";
            }

            builder.enableAutoRefresh();

            UserInfo userInfo = new UserInfo(builder);
            userInfo.verifFetchNeeded(false);

            userInfo_cache.put(code, userInfo);

            startAway(() -> {
                try {
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return "<meta http-equiv=\"refresh\" content=\"0; url=https://devarea.fr/?code=" + code + "\" />";
    }

    public static void save() throws IOException {
        HashMap<String, String> code_link_id = new HashMap<>();
        for (Map.Entry<String, UserInfo> random : userInfo_cache.entrySet())
            code_link_id.put(random.getKey(), random.getValue().getId());
        mapper.writeValue(new File("auth.json"), code_link_id);
    }

    public static void load() throws IOException {
        File file = new File("auth.json");
        if (!file.exists())
            save();
        HashMap<String, String> code_link_id = new HashMap<>();
        code_link_id = mapper.readValue(file, new TypeReference<>() {
        });
        for (Map.Entry<String, String> random : code_link_id.entrySet())
            userInfo_cache.put(random.getKey(), new UserInfo(random.getValue()));
    }

    public static boolean isMember(String id) {
        return Init.membersId.contains(Snowflake.of(id));
    }

    public static boolean isAlreadyBind(String id) {
        for (Map.Entry<String, UserInfo> random : userInfo_cache.entrySet())
            if (random.getValue().getId().equals(id))
                return true;
        return false;
    }

    public static String getUserLink(String id) {
        for (Map.Entry<String, UserInfo> random : userInfo_cache.entrySet())
            if (random.getValue().getId().equals(id))
                return random.getKey();
        return null;
    }

    public static UserInfo getInfoFor(String code) {
        return userInfo_cache.get(code);
    }

}
