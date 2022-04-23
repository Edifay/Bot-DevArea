package devarea.backend.controllers.rest;

import devarea.backend.controllers.tools.WebRoleCount;
import devarea.backend.controllers.tools.WebXPMember;
import devarea.backend.controllers.rest.requestContent.RequestHandlerStats;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static devarea.backend.controllers.rest.requestContent.RequestHandlerStats.getRoleCountList;
import static devarea.backend.controllers.rest.requestContent.RequestHandlerStats.getXpMembers;

@CrossOrigin()
@RestController
public class ControllerStats {

    @GetMapping(value = "stats/rolesCount_list", produces = MediaType.APPLICATION_JSON_VALUE)
    public static WebRoleCount[] rolesCounts_list(@RequestParam(value = "roles", defaultValue = "") String rolesString) {
        return getRoleCountList(rolesString);
    }

    @GetMapping(value = "/stats/xp_list")
    public static WebXPMember[] xp_list(@RequestParam(value = "start", defaultValue = "0") final int start,
                                        @RequestParam(value = "end", defaultValue = "50") final int end) {
        return getXpMembers(start, end);
    }

    @GetMapping(value = "/stats/member_count")
    public static int getMemberCount() {
        return RequestHandlerStats.getMemberCount();
    }


}