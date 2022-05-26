package devarea.backend.controllers.rest;

import devarea.backend.controllers.rest.requestContent.RequestHandlerFreelances;
import devarea.backend.controllers.tools.WebFreelance;
import devarea.bot.automatical.FreeLanceHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin()
@RestController
public class ControllerFreelances {

    @GetMapping("freelances/preview")
    public static WebFreelance.WebFreelancePreview[] preview(@RequestParam(value = "start", defaultValue = "0") int start,
                                                             @RequestParam(value = "end", defaultValue = "5") int end) {
        return RequestHandlerFreelances.freelancesToFreelancesPreview(FreeLanceHandler.getFreelances(start, end));
    }
}
