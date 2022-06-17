package devarea.backend.controllers.tools.badges.time_badge;

import devarea.Main;
import devarea.bot.Init;

public class Senior_Badge extends TimeOnServerBadge {

    public Senior_Badge(final String on_server_time) {
        super("Senior", Main.domainName + "assets/images/badges/70x70/time_badges/senior_badge.png", on_server_time,
                Init.senior_badge);
    }

}
