package devarea.global.badges.role_badge;

import devarea.Main;
import devarea.bot.Init;

public class Admin_Badge extends RolesBadges {
    public Admin_Badge() {
        super("Admin", Main.domainName + "assets/images/badges/70x70/roles_badges/admin_badge.png", "Ce membre est " +
                "administrateur sur le serveur !", Init.admin_badge);
    }
}
