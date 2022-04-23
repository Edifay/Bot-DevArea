package devarea.backend.controllers.rest.requestContent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import devarea.backend.controllers.tools.WebRoleCount;
import devarea.backend.controllers.tools.WebXPMember;
import devarea.bot.cache.MemberCache;
import devarea.bot.cache.RoleCache;
import devarea.bot.automatical.XPHandler;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;

public class RequestHandlerStats {

    public static WebRoleCount[] getRoleCountList(String rolesString) {
        try {

            String[] rolesId = RequestHandlerGlobal.mapper.readValue("[" + rolesString + "]",
                    new TypeReference<>() {
                    });

            WebRoleCount[] roleCounts = new WebRoleCount[rolesId.length];

            for (int i = 0; i < rolesId.length; i++) {
                Role role = RoleCache.watch(rolesId[i]);
                roleCounts[i] = new WebRoleCount(RoleCache.count(rolesId[i]), role.getId().asString(), role.getName());
                roleCounts[i].setColor(String.format("#%06X", (0xFFFFFF & role.getColor().getRGB())));
            }

            return roleCounts;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static WebXPMember[] getXpMembers(int start, int end) {
        WebXPMember[] members = XPHandler.getListOfIndex(start, end);
        for (WebXPMember member : members) {
            Member member_cached = MemberCache.watch(member.getId());
            member.setName(member_cached.getDisplayName());
            member.setUrlAvatar(member_cached.getAvatarUrl());
        }
        return members;
    }

    public static int getMemberCount() {
        return MemberCache.cacheSize();
    }

}
