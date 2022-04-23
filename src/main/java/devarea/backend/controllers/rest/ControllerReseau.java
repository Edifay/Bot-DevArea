package devarea.backend.controllers.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import devarea.backend.controllers.tools.WebReseau;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

import static devarea.backend.controllers.rest.requestContent.RequestHandlerGlobal.getObjectsFromJson;


@CrossOrigin()
@RestController
public class ControllerReseau {

    @GetMapping(value = "reseaux/links_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebReseau[] links_list() {
        try {
            return (WebReseau[]) getObjectsFromJson("data/reseau.json", new TypeReference<WebReseau[]>() {
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
