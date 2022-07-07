package devarea.global.badges.time_badge;

import devarea.Main;
import devarea.bot.Init;

public class Precursor_Badge extends TimeOnServerBadge {

    public Precursor_Badge(final String on_server_time) {
        super("Précurseur", Main.domainName + "assets/images/badges/70x70/time_badges/precursor_badge.png",
                on_server_time, Init.badgesImages.get("precursor_badge"));
    }
}
