package devarea.global.badges.xp_badge;

import devarea.Main;
import devarea.bot.Init;

import java.awt.image.BufferedImage;

public class OccasionedMember_Badge extends XPBadges {
    public OccasionedMember_Badge() {
        super("Membre Occasioné", Main.domainName + "assets/images/badges/70x70/xp_badges/occasionedmember_badge.png",
                "occasioné", Init.badgesImages.get("occasionedMember_badge"));
    }
}
