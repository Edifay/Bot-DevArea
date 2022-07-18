package devarea.global.badges.role_badge;

import devarea.Main;
import devarea.bot.Init;

public class Fonda_Badge extends RolesBadges {
    public Fonda_Badge() {
        super("Fondateur", Main.domainName + "assets/images/badges/70x70/roles_badges/fonda_badge.png",
                "Ce membre est le fondateur de Dev'Area !", Init.badgesImages.get("fonda_badge"));
    }
}
