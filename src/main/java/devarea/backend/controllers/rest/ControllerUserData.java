package devarea.backend.controllers.rest;

import devarea.backend.controllers.tools.userInfos.WebPublicUserInfos;
import org.springframework.web.bind.annotation.*;

import static devarea.backend.controllers.rest.requestContent.RequestHandlerUserData.requestGetMemberProfile;
import static devarea.backend.controllers.rest.requestContent.RequestHandlerUserData.requestUpdateUserDescription;


@CrossOrigin()
@RestController
public class ControllerUserData {

    @GetMapping("user-data/member-profile")
    public static WebPublicUserInfos getMemberProfile(@RequestParam(value = "member_id", required = true) String id) {
        return requestGetMemberProfile(id);
    }

    @PostMapping("user-data/update-description")
    public static boolean updateUserDescription(@RequestBody(required = false) String description,
                                                @RequestParam(value = "code") String code) {
        return requestUpdateUserDescription(description, code);
    }
}
