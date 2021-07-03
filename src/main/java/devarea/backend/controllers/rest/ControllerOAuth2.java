package devarea.backend.controllers.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dependencies.auth.domain.User;
import dependencies.auth.main.OAuthBuilder;
import dependencies.auth.main.Response;
import devarea.backend.controllers.data.UserInfo;
import devarea.bot.Init;
import devarea.bot.automatical.XpCount;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin()
@RestController
public class ControllerOAuth2 {

    private static final String client_id = "579257697048985601";
    private static final String client_secret = "wsFl0_7BHDJ4gdE1x7t31rGWyba3VvvG";
    private static final String redirect_url = "https://devarea.fr/data/auth";

    private static final ObjectMapper mapper = new ObjectMapper();

    private static HashMap<String, UserInfo> builders = new HashMap<>();

    public static void init() throws IOException {
        load();
    }


    @GetMapping("/auth/get")
    public UserInfo get(@RequestParam(value = "code") final String code) throws IOException {
        if (builders.containsKey(code)) {
            System.out.println("This value is in !");
        } else {
            return new UserInfo();
        }
        return builders.get(code);
    }

    @GetMapping("/auth")
    public String auth(@RequestParam(value = "code") final String code) throws IOException {
        if (builders.containsKey(code)) {
            System.out.println("Ce code est déjà utilisé !");
        } else {

            OAuthBuilder builder = new OAuthBuilder(client_id, client_secret).setScopes(new String[]{"identify"}).setRedirectURI(redirect_url);

            if (Response.ERROR == builder.exchange(code))
                return "ERROR IN LOGIN TO DISCORD";

            if (isAlreadyBind(builder.getIdUserString())) {
                System.out.println("Cet user est déjà link ! Destruction de ce lien !");
                builder.revoke();
                return "<meta http-equiv=\"refresh\" content=\"0; url=http://193.26.14.69/?code=" + getUserLink(builder.getIdUserString()) + "\" />";
            }

            builder.enableAutoRefresh();

            User user = builder.getUser();

            UserInfo userInfo = new UserInfo(builder);
            userInfo.setId(user.getId());
            userInfo.setMember(isMember(user.getId())); // TODO
            userInfo.setName(user.getUsername());
            userInfo.setUrlAvatar(user.getAvatar());
            userInfo.setLastTimeFetch(System.currentTimeMillis());

            if (userInfo.isMember()) {
                System.out.println("Cette personne est membre !");
                userInfo.setRank(XpCount.getRankOf(Snowflake.of(userInfo.getId())));
                userInfo.setXp(XpCount.getXpOf(Snowflake.of(userInfo.getId())));
            }

            builders.put(code, userInfo);

            save();

        }

        return "<meta http-equiv=\"refresh\" content=\"0; url=http://193.26.14.69/?code=" + code + "\" />";
    }

    public static void save() throws IOException {
        mapper.writeValue(new File("auth.json"), builders);
    }

    public static void load() throws IOException {
        File file = new File("auth.json");
        if (!file.exists())
            save();
        builders = mapper.readValue(file, new TypeReference<>() {
        });
    }

    public static boolean isMember(String id) {
        List<Member> members = Init.devarea.getMembers().buffer().blockLast();
        for (Member member : members)
            if (member.getId().asString().equals(id))
                return true;
        return false;
    }

    public static boolean isAlreadyBind(String id) {
        for (Map.Entry<String, UserInfo> random : builders.entrySet())
            if (random.getValue().getId().equals(id))
                return true;
        return false;
    }

    public static String getUserLink(String id) {
        for (Map.Entry<String, UserInfo> random : builders.entrySet())
            if (random.getValue().getId().equals(id))
                return random.getKey();
        return null;
    }

}
