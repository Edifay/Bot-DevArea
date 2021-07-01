package devarea.backend.controllers.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import devarea.backend.controllers.data.RoleCount;
import devarea.bot.Init;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static devarea.backend.controllers.rest.ControllerFonction.getObjectsFromJson;

@CrossOrigin()
@RestController
public class ControllerStats {

    private final static HashMap<String, RoleCount> idToRole = new HashMap<>();
    private long lastTimeFetched = 0;


    @GetMapping(value = "stats/rolesCount_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public RoleCount[] rolesCounts_list() {
        synchronized (idToRole) {
            try {
                String[] rolesId = (String[]) getObjectsFromJson("data/stats/stats.json", new TypeReference<String[]>() {
                });
                boolean newRole = false;
                for (String id : rolesId) {
                    if (!contain(id)) {
                        idToRole.put(id, new RoleCount(id));
                        System.out.println("Add new roles !");
                        newRole = true;
                    }
                }

                if ((System.currentTimeMillis() - lastTimeFetched) > 600000 || newRole) {
                    System.out.println("Fetch !");
                    fetch();
                    lastTimeFetched = System.currentTimeMillis();
                }

                RoleCount[] atReturn = new RoleCount[rolesId.length];
                for (int i = 0; i < rolesId.length; i++)
                    atReturn[i] = get(rolesId[i]);

                return atReturn;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static void fetch() {
        List<Member> members = Init.devarea.getMembers().buffer().blockLast();

        for (Map.Entry<String, RoleCount> set : idToRole.entrySet()) {
            String key = set.getKey();
            RoleCount value = set.getValue();
            value.setCountMember(0);
            if (value.getName() == null) {
                Role role = Init.devarea.getRoleById(Snowflake.of(key)).block();
                value.setName(role.getName());
                value.setColor(role.getColor().getRGB());
                System.out.println("Set the name of : " + value.getName());
            }
        }

        for (Member member : members) {
            for (Map.Entry<String, RoleCount> set : idToRole.entrySet()) {
                String key = set.getKey();
                RoleCount value = set.getValue();
                if (member.getRoleIds().contains(Snowflake.of(key))) {
                    value.setCountMember(value.getCountMember() + 1);
                }
            }
        }
    }

    private static boolean contain(String id) {
        for (String key : idToRole.keySet())
            if (id.equals(key))
                return true;
        return false;
    }

    private static RoleCount get(String id) {
        for (Map.Entry<String, RoleCount> set : idToRole.entrySet()) {
            String key = set.getKey();
            RoleCount value = set.getValue();
            if (id.equals(key)) {
                return value;
            }
        }
        return null;
    }

}