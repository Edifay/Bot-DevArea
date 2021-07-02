package devarea.backend.controllers.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import devarea.backend.controllers.data.RoleCount;
import devarea.backend.controllers.data.XpMember;
import devarea.bot.Init;
import devarea.bot.automatical.XpCount;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin()
@RestController
public class ControllerStats {

    private final static HashMap<String, RoleCount> idToRole = new HashMap<>();
    private long lastTimeFetched = 0;


    @GetMapping(value = "stats/rolesCount_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public RoleCount[] rolesCounts_list(@RequestParam(value = "roles", defaultValue = "[]", required = true) String rolesString) {
        synchronized (idToRole) {
            try {

                String[] rolesId = ControllerFonction.mapper.readValue("[" + rolesString + "]", new TypeReference<String[]>() {
                });
                boolean newRole = false;
                for (String id : rolesId) {
                    if (!contain(id)) {
                        idToRole.put(id, new RoleCount(id));
                        newRole = true;
                    }
                }

                if ((System.currentTimeMillis() - lastTimeFetched) > 600000 || newRole) {
                    fetch();
                    lastTimeFetched = System.currentTimeMillis();
                }

                RoleCount[] atReturn = new RoleCount[rolesId.length];
                for (int i = 0; i < rolesId.length; i++)
                    atReturn[i] = get(rolesId[i]);

                return atReturn;
            } catch (JsonProcessingException e) {
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
                value.setColor(String.format("#%06X", (0xFFFFFF & role.getColor().getRGB())));
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


    // -----------------------------------------------------------------------------------------------------

    private static final ArrayList<XpMember> xpMembers = new ArrayList<>();

    @GetMapping(value = "/stats/xp_list")
    public XpMember[] xp_list(@RequestParam(value = "start", defaultValue = "0") final int start, @RequestParam(value = "end", defaultValue = "50") final int end) {
        synchronized (xpMembers) {
            XpMember[] members = XpCount.getListOfIndex(start, end);
            for (XpMember member : members) {
                if (contain(member)) {
                    XpMember memberInList = get(member);
                    member.setName(memberInList.getName());
                    member.setUrlAvatar(memberInList.getUrlAvatar());
                } else {
                    Member memberDiscord = Init.devarea.getMemberById(Snowflake.of(member.getId())).block();
                    xpMembers.add(member);
                    member.setName(memberDiscord.getDisplayName());
                    member.setUrlAvatar(memberDiscord.getAvatarUrl());
                }
            }
            return members;
        }
    }

    public static boolean contain(XpMember member) {
        synchronized (xpMembers) {
            for (XpMember memberList : xpMembers) {
                if (memberList.equals(member)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static XpMember get(XpMember member) {
        synchronized (xpMembers) {
            for (XpMember memberList : xpMembers) {
                if (memberList.equals(member)) {
                    return memberList;
                }
            }
        }
        return null;
    }

}