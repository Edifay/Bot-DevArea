package devarea.backend.controllers.rest.requestContent;

import devarea.backend.controllers.tools.WebFreelance;
import devarea.bot.commands.commandTools.FreeLance;

public class RequestHandlerFreelances {

    public static WebFreelance.WebFreelancePreview[] freelancesToFreelancesPreview(final FreeLance[] freelances) {
        WebFreelance.WebFreelancePreview[] freelancePreviews = new WebFreelance.WebFreelancePreview[freelances.length];
        for (int i = 0; i < freelances.length; i++)
            freelancePreviews[i] = new WebFreelance.WebFreelancePreview(freelances[i]);
        return freelancePreviews;
    }
}
