package devarea.backend.controllers.tools.badges.time_badge;

import devarea.Main;

public class Precursor_Badge extends TimeOnServerBadge {

    public Precursor_Badge(final String on_server_time) {
        super("Bagde du Précuseur", Main.domainName + "assets/images/badges/time_badges/precursor_badge.png",
                on_server_time);
    }
}
