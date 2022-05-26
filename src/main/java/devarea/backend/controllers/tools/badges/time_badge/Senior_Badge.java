package devarea.backend.controllers.tools.badges.time_badge;

import devarea.Main;

public class Senior_Badge extends TimeOnServerBadge{

    public Senior_Badge(final String on_server_time) {
        super("Bagde du Senior", Main.domainName +"assets/images/badges/time_badges/senior_badge.jpg", on_server_time);
    }

}
