package devarea.backend.controllers.rest;

import devarea.backend.controllers.rest.requestContent.RequestHandlerAuth;
import devarea.backend.controllers.tools.WebFreelance;
import devarea.backend.controllers.tools.userInfos.WebUserInfos;
import devarea.global.handlers.FreeLanceHandler;
import devarea.bot.commands.commandTools.FreeLance;
import org.springframework.web.bind.annotation.*;


import static devarea.backend.controllers.rest.requestContent.RequestHandlerFreelances.requestFreelancePreview;
import static devarea.backend.controllers.rest.requestContent.RequestHandlerFreelances.requestSetFreelance;

@CrossOrigin()
@RestController
public class ControllerFreelances {

    @GetMapping("freelances/preview")
    public static WebFreelance.WebFreelancePreview[] preview(@RequestParam(value = "start", defaultValue = "0") int start,
                                                             @RequestParam(value = "end", defaultValue = "5") int end) {
        return requestFreelancePreview(start, end);
    }


    @PostMapping("freelances/set")
    public static boolean setFreelance(@RequestBody() WebFreelance freelance,
                                       @RequestParam(value = "code") String code) {
        return requestSetFreelance(freelance, code);
    }

    @GetMapping("freelances/delete")
    public static boolean deleteFreelance(@RequestParam(value = "code") String code) {
        WebUserInfos infos = RequestHandlerAuth.get(code);
        if (infos != null && FreeLanceHandler.hasFreelance(infos.getId())) {
            FreeLance current = FreeLanceHandler.getFreelance(infos.getId());
            FreeLanceHandler.remove(current);
            current.delete();
            return true;
        }
        return false;
    }

    @GetMapping("freelances/bump")
    public static boolean bumpFreelance(@RequestParam(value = "code") String code) {
        WebUserInfos infos = RequestHandlerAuth.get(code);
        if (infos != null && FreeLanceHandler.hasFreelance(infos.getId())) {
            return FreeLanceHandler.bumpFreeLance(infos.getId());
        }
        return false;
    }


}
