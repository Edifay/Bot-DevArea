package devarea.backend.controllers.rest;

import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.charset.StandardCharsets;

@RestController
public class ControllerBack {

    @CrossOrigin
    @GetMapping(value = "staff_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public String staff_list() {
        String json = "";
        File file = new File("staff.json");
        if (!file.exists()) {
            try {
                PrintStream out = new PrintStream(file);
                out.print("[]");
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try (InputStream input = new FileInputStream("staff.json")) {
            json = StreamUtils.copyToString(input, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

}
