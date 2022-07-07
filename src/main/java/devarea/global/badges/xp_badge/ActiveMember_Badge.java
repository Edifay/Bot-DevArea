package devarea.global.badges.xp_badge;

import devarea.Main;
import devarea.bot.Init;


public class ActiveMember_Badge extends XPBadges {
    public ActiveMember_Badge() {
        super("Membre Actif", Main.domainName + "assets/images/badges/70x70/xp_badges/activemember_badge.png", "actif",
                Init.badgesImages.get("activeMember_badge"));
    }
}
