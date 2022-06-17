package devarea.backend.controllers.rest.requestContent;

import devarea.backend.controllers.tools.WebFreelance;
import devarea.backend.controllers.tools.userInfos.WebUserInfos;
import devarea.global.handlers.FreeLanceHandler;
import devarea.bot.commands.commandTools.FreeLance;

import java.util.ArrayList;

import static devarea.bot.event.FunctionEvent.startAway;

public class RequestHandlerFreelances {

    private static final ArrayList<String> coolDown = new ArrayList<>();

    public static WebFreelance.WebFreelancePreview[] requestFreelancePreview(int start, int end) {
        return RequestHandlerFreelances.freelancesToFreelancesPreview(FreeLanceHandler.getFreelances(start, end));
    }


    public static boolean requestSetFreelance(WebFreelance freelance, String code) {
        if (RequestHandlerAuth.containCode(code) && !coolDown.contains(code)) {
            WebUserInfos infos = RequestHandlerAuth.get(code);
            if (FreeLanceHandler.hasFreelance(infos.getId())) {
                FreeLance current = FreeLanceHandler.getFreelance(infos.getId());
                FreeLanceHandler.remove(current);

                freelance.member_id = infos.getId();

                FreeLance newOne = new FreeLance(freelance);
                newOne.setLast_bump(current.getLast_bump());
                newOne.setMessage(current.getMessage());
                FreeLanceHandler.add(newOne);

                newOne.edit();
            } else {
                freelance.member_id = infos.getId();

                FreeLance newOne = new FreeLance(freelance);
                FreeLanceHandler.add(newOne);

                newOne.send();
            }
            coolDown.add(code);
            startAway(() -> {
                try {
                    Thread.sleep(5000);
                } catch (Exception ignored) {
                } finally {
                    coolDown.remove(code);
                }
            });
            return true;
        }
        return false;
    }

    public static WebFreelance.WebFreelancePreview[] freelancesToFreelancesPreview(final FreeLance[] freelances) {
        WebFreelance.WebFreelancePreview[] freelancePreviews = new WebFreelance.WebFreelancePreview[freelances.length];
        for (int i = 0; i < freelances.length; i++)
            freelancePreviews[i] = new WebFreelance.WebFreelancePreview(freelances[i]);
        return freelancePreviews;
    }
}
