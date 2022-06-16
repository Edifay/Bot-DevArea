package devarea.backend.controllers.tools.badges.role_badge;

import devarea.Main;

public class Modo_Badge extends RolesBadges {
    public Modo_Badge() {
        super("Modérateur", Main.domainName + "assets/images/badges/roles_badges/modo_badge.png", "Ce membre est " +
                "modérateur sur le serveur !");
    }
}
