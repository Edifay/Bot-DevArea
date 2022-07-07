package devarea.global.badges.xp_badge;

import devarea.Main;
import devarea.bot.Init;

public class RegularMember_Badge extends XPBadges {
    public RegularMember_Badge() {
        super("Membre Régulier", Main.domainName + "assets/images/badges/70x70/xp_badges/regularmember_badge.png",
                "régulier", Init.badgesImages.get("regularMember_badge"));
    }
}
