package devarea.backend.controllers.tools.badges.role_badge;

import devarea.Main;

public class Graphiste_Badge extends RolesBadges {
    public Graphiste_Badge() {
        super("Graphiste", Main.domainName + "assets/images/badges/roles_badges/graphiste_badge.png", "Ce membre est " +
                "un Graphiste du serveur !");
    }
}
