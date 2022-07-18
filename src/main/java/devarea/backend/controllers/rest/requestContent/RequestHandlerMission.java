package devarea.backend.controllers.rest.requestContent;

import devarea.backend.controllers.tools.WebMission;
import devarea.backend.controllers.tools.userInfos.WebPrivateUserInfos;
import devarea.global.cache.MemberCache;
import devarea.global.handlers.MissionsHandler;
import devarea.bot.commands.commandTools.Mission;
import discord4j.core.object.entity.Member;

import java.util.ArrayList;
import java.util.List;

import static devarea.global.utils.ThreadHandler.startAway;

public class RequestHandlerMission {


    public static WebMission.WebMissionPreview[] requestPreviewMission(int start, int end) {
        final ArrayList<Mission> missions = MissionsHandler.getMissions();
        final int size = missions.size();

        if (start > end) end = start;
        if (end > size) end = size;
        if (start > size) start = size;

        return transformMissionsToWebMissionsPreview(missions.subList(start, end)).toArray(new WebMission.WebMissionPreview[0]);
    }

    public static WebMission requestGetMission(final String id) {
        return MissionsHandler.get(id).toWebMission();
    }

    public static String requestTookMission(String missionID, String code) {
        Member member = RequestHandlerAuth.get(code).getMember();
        return MissionsHandler.tookMissionFromWeb(missionID, member);
    }

    public static String[] requestDeleteMission(String id, String code) {
        WebPrivateUserInfos user = RequestHandlerAuth.get(code);
        if (user != null) {
            Mission mission = MissionsHandler.get(id);
            if (mission != null) {
                MissionsHandler.clearThisMission(mission);
                return new String[]{"deleted"};
            }
            return new String[]{"mission_not_found"};
        } else
            return new String[]{"wrong_code"};
    }


    public static final ArrayList<String> cooldown_create_Mission = new ArrayList<>();

    public static boolean requestCreateMission(final ReceiveMission mission, final String code) {
        WebPrivateUserInfos infos = RequestHandlerAuth.get(code);
        System.out.println("Infos : " + infos);
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

    public static ArrayList<WebMission.WebMissionPreview> transformMissionsToWebMissionsPreview(List<Mission> list) {
        ArrayList<WebMission.WebMissionPreview> list_transformed = new ArrayList<>();

        for (Mission miss : list)
            if (MemberCache.contain(miss.getMemberId()))
                list_transformed.add(new WebMission(miss).toPreview());
        return list_transformed;
    }
}
