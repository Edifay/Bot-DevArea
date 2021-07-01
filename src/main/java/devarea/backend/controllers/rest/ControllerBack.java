package devarea.backend.controllers.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.backend.controllers.data.Staff;
import devarea.bot.Init;
import discord4j.common.util.Snowflake;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@RestController
public class ControllerBack {
    private final static ObjectMapper mapper = new ObjectMapper();
    private final static HashMap<String, String> idToUrl = new HashMap<>();

    @CrossOrigin
    @GetMapping(value = "staff_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Staff[] staff_list() {

        Staff[] defaultStaff = new Staff[1];
        defaultStaff[0] = new Staff("ERREUR !", "JSON data cannot be read !", "https://cdn.discordapp.com/avatars/321673326105985025/6c57fad5a817b898d1056201154b7b46.png");

        File file = new File("staff.json"); // check if file exist ! And create it if not !
        if (!file.exists())
            try {
                PrintStream out = new PrintStream(file);
                out.print("[]");
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        try (InputStream input = new FileInputStream("staff.json")) { // load file !

            String data = StreamUtils.copyToString(input, StandardCharsets.UTF_8);
            Staff[] staffs = mapper.readValue(data, new TypeReference<Staff[]>() {
            });

            for (Staff staff : staffs) { // load url with id !
                if (!idToUrl.containsKey(staff.getId())) {
                    staff.setUrlAvatar(Init.devarea.getMemberById(Snowflake.of(staff.getId())).block().getAvatarUrl());
                    idToUrl.put(staff.getId(), staff.getUrlAvatar());
                } else
                    staff.setUrlAvatar(idToUrl.get(staff.getId()));
                staff.resetId();
            }

            return staffs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defaultStaff;
    }

}
