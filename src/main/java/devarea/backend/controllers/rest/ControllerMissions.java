package devarea.backend.controllers.rest;

import devarea.backend.controllers.data.MissionForWeb;
import devarea.bot.Init;
import devarea.bot.automatical.MissionsManager;
import devarea.bot.commands.object_for_stock.Mission;
import discord4j.common.util.Snowflake;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin()
@RestController
public class ControllerMissions {

    protected static ArrayList<MissionForWeb> cache = new ArrayList<>();

    @GetMapping("missions/get")
    public MissionForWeb[] get(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "end", defaultValue = Integer.MAX_VALUE + "") int end) {
        if (start > end)
            end = start;

        if (end > MissionsManager.getMissions().size())
            end = MissionsManager.getMissions().size();

        if (start > MissionsManager.getMissions().size())
            start = MissionsManager.getMissions().size();

        List<Mission> list = MissionsManager.getMissions().subList(start, end);
        ArrayList<MissionForWeb> list_transformed = new ArrayList<>();

        for (Mission miss : list)
            if (Init.membersId.contains(Snowflake.of(miss.getMemberId())))
                if (cache.contains(miss)) {
                    MissionForWeb mission_for_web = cache.get(cache.indexOf(miss));
                    mission_for_web.verifyFetch(false);
                    list_transformed.add(mission_for_web);
                } else {
                    MissionForWeb mission_for_web = new MissionForWeb(miss);
                    list_transformed.add(mission_for_web);
                    cache.add(mission_for_web);
                }

        return list_transformed.toArray(new MissionForWeb[0]);
    }

}
