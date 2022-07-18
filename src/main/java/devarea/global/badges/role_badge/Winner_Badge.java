package devarea.global.badges.role_badge;

import devarea.Main;
import devarea.bot.Init;

public class Winner_Badge extends RolesBadges {
    public Winner_Badge() {
        super("Contest Winner", Main.domainName + "assets/images/badges/70x70/roles_badges/winner_badge.png",
                "Ce membre a fini sur le podium du dernier Contest organisé par le serveur !", Init.badgesImages.get("winner_badge"));
    }
}
