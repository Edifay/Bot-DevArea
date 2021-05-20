package devarea.automatical;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.Main;
import devarea.commands.ObjetForStock.RoleReact;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Member;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
                System.out.println(str);
                try {
                    newHash = mapper.readValue(str, new TypeReference<>() {
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                after.put(newHash, v);
            });

            after.forEach((hash, role) -> rolesReacts.put(new RoleReact(hash), Snowflake.of(role)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void onReact(ReactionAddEvent event) {
        rolesReacts.forEach((k, v) -> {
            try {
                if (k.is(event)) {
                    System.out.println("Ajout de rôle detected:");
                    assert event.getMember().isPresent();
                    Member member = event.getMember().get();
                    member.addRole(v).subscribe();
                    System.out.println("Le rôle : " + Main.devarea.getRoleById(v).block().getName() + " a été ajouter à : " + member.getDisplayName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized static void onRemoveReact(ReactionRemoveEvent event) {
        rolesReacts.forEach((k, v) -> {
            try {
                if (k.is(event)) {
                    System.out.println("Detect remove :");
                    Member member = Main.devarea.getMemberById(event.getUserId()).block();
                    assert member != null;
                    member.removeRole(v).subscribe();
                    System.out.println("Le rôle : " + Main.devarea.getRoleById(v).block().getName() + " a été retirer a : " + member.getDisplayName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
