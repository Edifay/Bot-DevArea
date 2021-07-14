package devarea.bot.automatical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.bot.Init;
import devarea.bot.commands.object_for_stock.RoleReact;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Member;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class RolesReacts {

    public static HashMap<RoleReact, Snowflake> rolesReacts = new HashMap<>();

    public static void init() {
        load();
    }

    public static void save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            final HashMap<HashMap<String, String>, String> stock = new HashMap<>();
            rolesReacts.forEach((roleReact, role) -> stock.put(roleReact.getHashMap(), role.asString()));
            mapper.writeValue(new File("./roleReact.json"), stock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        File file = new File("./roleReact.json");
        if (!file.exists()) {
            save();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            HashMap<String, String> obj = mapper.readValue(file, new TypeReference<>() {
            });
            HashMap<HashMap<String, String>, String> after = new HashMap<>();
            obj.forEach((k, v) -> {
                HashMap<String, String> newHash = null;
                String str = k.replace("reactionId=", "\"reactionId\":\"").replace(", message={", "\", \"message\":\"{").replace("}}", "}\"}").replace("isID=", "\"isID\":").replace("false", "\"false\"").replace("true", "\"true\"");
                try {
                    newHash = mapper.readValue(str, new TypeReference<>() {
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                after.put(newHash, v);
            });

            after.forEach((hash, role) -> rolesReacts.put(new RoleReact(hash), Snowflake.of(role)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static boolean onReact(ReactionAddEvent event) {
        AtomicBoolean haveAdded = new AtomicBoolean(false);
        rolesReacts.forEach((k, v) -> {
            try {
                if (k.is(event)) {
                    assert event.getMember().isPresent();
                    Member member = event.getMember().get();
                    member.addRole(v).subscribe();
                    haveAdded.set(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return haveAdded.get();
    }

    public synchronized static void onRemoveReact(ReactionRemoveEvent event) {
        rolesReacts.forEach((k, v) -> {
            try {
                if (k.is(event)) {
                    Member member = Init.devarea.getMemberById(event.getUserId()).block();
                    assert member != null;
                    member.removeRole(v).subscribe();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
