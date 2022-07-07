package devarea.global.badges.success_badge;

import devarea.Main;
import devarea.bot.Init;

public class Profile_Badge extends SuccessBadge {
    public Profile_Badge() {
        super("Profil completé", Main.domainName + "assets/images/badges/70x70/success_badges/profile_badge.png", "Ce" +
                " " +
                "membre a complété son profil !", Init.badgesImages.get("profile_badge"));
    }
}
