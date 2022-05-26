package devarea.backend.controllers.tools.badges.time_badge;

import devarea.Main;

public class Junior_Badge extends TimeOnServerBadge {
    public Junior_Badge(final String on_server_time) {
        super("Bagde Junior", Main.domainName + "assets/images/badges/time_badges/junior_badge.png", on_server_time);
    }
}
