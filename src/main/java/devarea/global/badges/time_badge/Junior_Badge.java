package devarea.global.badges.time_badge;

import devarea.Main;
import devarea.bot.Init;

public class Junior_Badge extends TimeOnServerBadge {
    public Junior_Badge(final String on_server_time) {
        super("Junior", Main.domainName + "assets/images/badges/70x70/time_badges/junior_badge.png", on_server_time
                , Init.junior_badge);
    }
}
