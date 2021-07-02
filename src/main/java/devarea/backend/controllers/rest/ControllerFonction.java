package devarea.backend.controllers.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ControllerFonction {

    public final static ObjectMapper mapper = new ObjectMapper();

    public static Object[] getObjectsFromJson(final String url, TypeReference reference) throws FileNotFoundException {
        File file = new File(url); // check if file exist ! And create it if not !
        if (!file.exists()) {
            PrintStream out = new PrintStream(file);
            out.print("[]");
            out.flush();
            out.close();
        }

        try (InputStream input = new FileInputStream(url)) { // load file !

            String data = StreamUtils.copyToString(input, StandardCharsets.UTF_8);
            Object[] objects = (Object[]) mapper.readValue(data, reference);

            return objects;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
