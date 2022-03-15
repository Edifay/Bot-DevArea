package devarea.backend.controllers.rest;

import devarea.backend.controllers.data.MissionForWeb;
import devarea.backend.controllers.data.UserInfo;
import devarea.bot.Init;
import devarea.bot.automatical.MissionsManager;
import devarea.bot.commands.object_for_stock.Mission;
import discord4j.common.util.Snowflake;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CrossOrigin()
@RestController
public class ControllerMissions {

    protected static ArrayList<MissionForWeb> cache = new ArrayList<>();

    @GetMapping("missions/get")
    public static MissionForWeb[] get(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "end", defaultValue = Integer.MAX_VALUE + "") int end) {
        if (start > end)
            end = start;

        if (end > MissionsManager.getMissions().size())
            end = MissionsManager.getMissions().size();

        if (start > MissionsManager.getMissions().size())
            start = MissionsManager.getMissions().size();

        ArrayList<Mission> list_at_reverse = (ArrayList<Mission>) MissionsManager.getMissions().clone();
        Collections.reverse(list_at_reverse);
        List<Mission> list = list_at_reverse.subList(start, end);
        ArrayList<MissionForWeb> list_transformed = transformMissionListToMissionWebList(list);

        return list_transformed.toArray(new MissionForWeb[0]);
    }

    @GetMapping("missions/delete")
    public static String[] delete(@RequestParam(value = "message_id") String message_id, @RequestParam(value = "code") String code) {
        UserInfo user = ControllerOAuth2.getInfoFor(code);
        if (user != null) {
            ArrayList<Mission> missions = MissionsManager.getOf(user.getAsSnowflake());

            for (Mission mission : missions)
                if (mission.getMessage().getMessageID().asString().equals(message_id)) {
                    MissionsManager.clearThisMission(mission);
                    return new String[]{"deleted"};
                }

            return new String[]{"mission_not_find"};
        } else
            return new String[]{"wrong_code"};
    }

    public static ArrayList<MissionForWeb> transformMissionListToMissionWebList(List<Mission> list) {
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
        return list_transformed;
    }

}
