package devarea.global.badges.role_badge;

import devarea.Main;
import devarea.bot.Init;

public class Partner_Badge extends RolesBadges {
    public Partner_Badge() {
        super("Partenaire", Main.domainName + "assets/images/badges/70x70/roles_badges/partner_badge.png",
                "Ce membre est partenaire avec le serveur !", Init.badgesImages.get("partner_badge"));
    }
}
