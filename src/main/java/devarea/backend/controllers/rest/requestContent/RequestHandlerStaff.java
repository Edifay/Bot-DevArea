package devarea.backend.controllers.rest.requestContent;

import com.fasterxml.jackson.core.type.TypeReference;
import devarea.backend.controllers.tools.WebStaff;
import devarea.bot.cache.MemberCache;
import discord4j.core.object.entity.Member;

import java.io.FileNotFoundException;

import static devarea.backend.controllers.rest.requestContent.RequestHandlerGlobal.getObjectsFromJson;

public class RequestHandlerStaff {

    public static WebStaff[] staffs;

    static {
        try {
            staffs = (WebStaff[]) getObjectsFromJson("data/staff.json", new TypeReference<WebStaff[]>() {
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static WebStaff[] requestGetStaffList() {

        for (WebStaff staff : staffs) {
            Member member = MemberCache.get(staff.getId());
            staff.setUrlAvatar(member.getAvatarUrl());
            staff.setName(member.getDisplayName());
        }

        for (int i = 0; i < staffs.length; i++)
            staffs[i].setIdCss(i % 2f != 0f ? "pair" : "impair");

        return staffs;
    }

}
