package devarea.global.badges.role_badge;

import devarea.Main;
import devarea.bot.Init;

public class Modo_Badge extends RolesBadges {
    public Modo_Badge() {
        super("Modérateur", Main.domainName + "assets/images/badges/70x70/roles_badges/modo_badge.png", "Ce membre est " +
                "modérateur sur le serveur !", Init.modo_badge);
    }
}
