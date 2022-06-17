package devarea.backend.controllers.tools.badges.time_badge;

import devarea.Main;
import devarea.bot.Init;

public class Junior_Badge extends TimeOnServerBadge {
    public Junior_Badge(final String on_server_time) {
        super("Junior", Main.domainName + "assets/images/badges/time_badges/junior_badge.png", on_server_time
                , Init.junior_badge);
    }
}
