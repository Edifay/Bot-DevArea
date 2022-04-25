package devarea.backend.controllers.rest;

import devarea.backend.controllers.tools.WebMission;
import org.springframework.web.bind.annotation.*;

import static devarea.backend.controllers.rest.requestContent.RequestHandlerMission.*;

@CrossOrigin()
@RestController
public class ControllerMissions {


    @GetMapping("missions/preview")
    public static WebMission.WebMissionPreview[] preview(@RequestParam(value = "start", defaultValue = "0") int start,
                                                         @RequestParam(value = "end", defaultValue =
                                                                 Integer.MAX_VALUE + "") int end) {
        return requestPreviewMission(start, end);
    }

    @GetMapping("missions/get")
    public static WebMission getMission(@RequestParam(value = "id", required = true) String id) {
        return requestGetMission(id);
    }

    @GetMapping("missions/took")
    public static String[] tookMission(@RequestParam(value = "missionID", required = true) String missionID,
                                       @RequestParam(value = "code", required = true) String code) {
        return new String[]{requestTookMission(missionID, code)};
    }

    @GetMapping("missions/delete")
    public static String[] delete(@RequestParam(value = "missionID") String id,
                                  @RequestParam(value = "code") String code) {
        return requestDeleteMission(id, code);
    }

    @PostMapping("missions/create")
    public static boolean postMission(@RequestBody ReceiveMission mission, @RequestParam(value = "code") String code) {
        return requestCreateMission(mission, code);
    }

}
