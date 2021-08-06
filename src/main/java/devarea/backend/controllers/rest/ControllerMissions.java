package devarea.backend.controllers.rest;

import devarea.bot.automatical.MissionsManager;
import devarea.bot.commands.object_for_stock.Mission;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin()
@RestController
public class ControllerMissions {

    @GetMapping("missions/get")
    public Mission[] get(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "end", defaultValue = Integer.MAX_VALUE + "") int end) {
        if (start > end)
            end = start;

        if (end > MissionsManager.getMissions().size())
            end = MissionsManager.getMissions().size();

        if (start > MissionsManager.getMissions().size())
            start = MissionsManager.getMissions().size();
        return MissionsManager.getMissions().subList(start, end).toArray(new Mission[0]);
    }

}
