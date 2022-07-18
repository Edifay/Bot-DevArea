package devarea.global.badges.role_badge;

import devarea.Main;
import devarea.bot.Init;

public class Graphist_Badge extends RolesBadges {
    public Graphist_Badge() {
        super("Graphiste", Main.domainName + "assets/images/badges/70x70/roles_badges/graphist_badge.png",
                "Ce membre est un Graphiste du serveur !", Init.badgesImages.get("graphist_badge"));
    }
}
