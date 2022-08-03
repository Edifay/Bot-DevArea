package devarea.bot.automatical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.bot.Init;
import devarea.bot.commands.commandTools.RoleReact;
import devarea.global.cache.MemberCache;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Member;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class RolesReactsHandler {

    private static HashMap<RoleReact, Snowflake> rolesReacts = new HashMap<>();

    public static void init() {
        load();
    }

    public static void save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            final HashMap<String, RoleReact> invertedMap = new HashMap<>();
            rolesReacts.forEach((k, v) -> invertedMap.put(v.asString(), k));
            mapper.writeValue(new File("./roleReact.json"), invertedMap);
            System.out.println("Save !");
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
            final HashMap<String, RoleReact> temp = mapper.readValue(file, new TypeReference<>() {
            });

            temp.forEach((k, v) -> rolesReacts.put(v, Snowflake.of(k)));

            System.out.println("RolesReact loaded : " + rolesReacts.size() + " detected !");
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
                    MemberCache.reset(member.getId().asString());
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
                    Member member = MemberCache.get(event.getUserId().asString());
                    assert member != null;
                    member.removeRole(v).subscribe();
                    MemberCache.reset(member.getId().asString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ---------------- UTILS ----------------

    public static int getRoleReactCount() {
        return rolesReacts.size();
    }

    /*
        Setup sorted by message list !
     */
    public static String getListByMessageRolesReact(RoleReact[] removeTable) {
        StringBuilder str = new StringBuilder();

        ArrayList<Snowflake> uniqueMessageAlreadyComplete = new ArrayList<>();
        int setted = 0;

        for (Map.Entry<RoleReact, Snowflake> entry : rolesReacts.entrySet()) {
            RoleReact roleReact = entry.getKey();
            Snowflake v = entry.getValue();
            Snowflake currentMessageID = roleReact.getMessageSeria().getMessageID();

            if (!uniqueMessageAlreadyComplete.contains(currentMessageID)) { // New message detection

                // set URL message
                str.append("https://discord.com/channels/").append(Init.devarea.getId().asString()).append("/").append(roleReact.getMessageSeria().getChannelID().asString()).append("/").append(currentMessageID.asString()).append(" :\n");

                for (Map.Entry<RoleReact, Snowflake> e : rolesReacts.entrySet()) {
                    RoleReact roleReactToSet = e.getKey();
                    Snowflake roleId = e.getValue();
                    if (roleReactToSet.getMessageSeria().getMessageID().equals(currentMessageID)) { // add only
                        // rolereact on same message
                        str.append("`").append(setted).append("`:").append(roleReactToSet.getStringEmoji()).append(" " +
                                "-> <@&").append(roleId.asString()).append(">\n");
                        removeTable[setted] = roleReactToSet;
                        setted++;
                    }
                }

                uniqueMessageAlreadyComplete.add(currentMessageID);
            }
        }
        return str.toString();
    }

    public synchronized static void addNewRoleReact(final RoleReact roleReact, final Snowflake roleId) {
        rolesReacts.put(roleReact, roleId);
    }

    public synchronized static void removeRoleReact(final RoleReact roleReact) {
        rolesReacts.remove(roleReact);
    }

}
