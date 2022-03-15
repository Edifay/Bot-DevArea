package devarea.backend.controllers.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import devarea.backend.controllers.data.Staff;
import devarea.bot.Init;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.HashMap;

import static devarea.backend.controllers.rest.ControllerFonction.getObjectsFromJson;

@CrossOrigin()
@RestController
public class ControllerStaff {

    private final static HashMap<String, String[]> idToUrl = new HashMap<>();

    @GetMapping(value = "staff/staff_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Staff[] staff_list() {

        Staff[] defaultStaff = new Staff[1];
        defaultStaff[0] = new Staff("ERREUR !", "JSON data cannot be read !", "https://cdn.discordapp.com/avatars/321673326105985025/6c57fad5a817b898d1056201154b7b46.png");

        try {

            Staff[] staffs = (Staff[]) getObjectsFromJson("data/staff.json", new TypeReference<Staff[]>() {
            });
            for (Staff staff : staffs) { // load url with id !
                if (!idToUrl.containsKey(staff.getId())) {
                    Member member = Init.devarea.getMemberById(Snowflake.of(staff.getId())).block();
                    staff.setUrlAvatar(member.getAvatarUrl());
                    staff.setName(member.getDisplayName());
                    idToUrl.put(staff.getId(), new String[]{member.getDisplayName(), member.getAvatarUrl()});
                } else {
                    staff.setName(idToUrl.get(staff.getId())[0]);
                    staff.setUrlAvatar(idToUrl.get(staff.getId())[1]);
                }
                staff.resetId();
            }
            for (int i = 0; i < staffs.length; i++)
                staffs[i].setIdCss(i % 2f != 0f ? "pair" : "impair");
            return staffs;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return defaultStaff;
    }

}
