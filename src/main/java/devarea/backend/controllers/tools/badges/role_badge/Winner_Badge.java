package devarea.backend.controllers.tools.badges.role_badge;

import devarea.Main;
import devarea.bot.Init;

public class Winner_Badge extends RolesBadges {
    public Winner_Badge() {
        super("Contest Winner", Main.domainName + "assets/images/badges/roles_badges/winner_badge.png", "Vous avez" +
                " finis sur le podium du dernier Contest organisé par le serveur !", Init.winner_badge);
    }
}
