package devarea.backend.controllers.rest;

import devarea.backend.controllers.tools.WebMission;
import org.springframework.web.bind.annotation.*;

import static devarea.backend.controllers.rest.requestContent.RequestHandlerMission.*;

@CrossOrigin()
@RestController
public class ControllerMissions {


    @GetMapping("missions/get")
    public static WebMission[] get(@RequestParam(value = "start", defaultValue = "0") int start,
                                   @RequestParam(value = "end", defaultValue = Integer.MAX_VALUE + "") int end) {
        return requestGetMissions(start, end);
    }


    @GetMapping("missions/delete")
    public static String[] delete(@RequestParam(value = "message_id") String message_id,
                                  @RequestParam(value = "code") String code) {
        return requestDeleteMission(message_id, code);
    }

    @PostMapping("missions/create")
    public static boolean postMission(@RequestBody ReceiveMission mission, @RequestParam(value = "code") String code) {
        return requestCreateMission(mission, code);
    }

}
