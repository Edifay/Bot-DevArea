package devarea.backend.controllers.rest.requestContent;

import devarea.backend.controllers.tools.WebMission;
import devarea.backend.controllers.tools.WebUserInfo;
import devarea.bot.cache.MemberCache;
import devarea.bot.automatical.MissionsHandler;
import devarea.bot.commands.commandTools.Mission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static devarea.bot.event.FunctionEvent.startAway;

public class RequestHandlerMission {


    public static WebMission[] requestGetMissions(int start, int end) {
        if (start > end)
            end = start;

        if (end > MissionsHandler.getMissions().size())
            end = MissionsHandler.getMissions().size();

        if (start > MissionsHandler.getMissions().size())
            start = MissionsHandler.getMissions().size();

        ArrayList<Mission> list_at_reverse = (ArrayList<Mission>) MissionsHandler.getMissions().clone();
        Collections.reverse(list_at_reverse);
        List<Mission> list = list_at_reverse.subList(start, end);
        ArrayList<WebMission> list_transformed = transformMissionListToMissionWebList(list);

        return list_transformed.toArray(new WebMission[0]);
    }


    public static String[] requestDeleteMission(String message_id, String code) {
        WebUserInfo user = RequestHandlerAuth.get(code);
        if (user != null) {
            ArrayList<Mission> missions = MissionsHandler.getOf(user.getAsSnowflake());

            for (Mission mission : missions)
                if (mission.getMessage().getMessageID().asString().equals(message_id)) {
                    MissionsHandler.clearThisMission(mission);
                    return new String[]{"deleted"};
                }

            return new String[]{"mission_not_find"};
        } else
            return new String[]{"wrong_code"};
    }


    public static final ArrayList<String> cooldown_create_Mission = new ArrayList<>();

    public static boolean requestCreateMission(final ReceiveMission mission, final String code) {
        WebUserInfo infos = RequestHandlerAuth.get(code);
        System.out.println("infos : " + infos);
        System.out.println("Can post : " + !cooldown_create_Mission.contains(code));
        if (infos != null && !cooldown_create_Mission.contains(code)) {
            cooldown_create_Mission.add(code);

            MissionsHandler.createMission(mission.title, mission.description, mission.budget, mission.dateRetour,
                    mission.langage, mission.support, mission.niveau, infos.getMember());

            startAway(() -> {
                try {
                    Thread.sleep(5000);
                    cooldown_create_Mission.remove(code);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        return false;
    }

    public static class ReceiveMission {

        public String title;
        public String description;
        public String dateRetour;
        public String langage;
        public String support;
        public String niveau;
        public String budget;

    }

    public static ArrayList<WebMission> transformMissionListToMissionWebList(List<Mission> list) {
        ArrayList<WebMission> list_transformed = new ArrayList<>();

        for (Mission miss : list)
            if (MemberCache.contain(miss.getMemberId()))
                list_transformed.add(new WebMission(miss));
        return list_transformed;
    }
}
