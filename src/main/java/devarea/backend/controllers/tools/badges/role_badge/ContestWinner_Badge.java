package devarea.backend.controllers.tools.badges.role_badge;

import devarea.Main;

public class ContestWinner_Badge extends RolesBadges {
    public ContestWinner_Badge() {
        super("Contest Winner", Main.domainName + "assets/images/badges/roles_badges/contestwinner_badge.png", "Vous avez" +
                " finis sur le podium du dernier Contest organisé par le serveur !");
    }
}
