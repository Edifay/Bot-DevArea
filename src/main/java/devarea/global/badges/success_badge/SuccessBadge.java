package devarea.global.badges.success_badge;

import devarea.global.handlers.UserInfosHandler;
import devarea.global.badges.Badges;
import devarea.backend.controllers.tools.userInfos.WebUserInfos;
import discord4j.core.object.entity.Member;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class SuccessBadge extends Badges {
    public SuccessBadge(String name, String url_icon, String description, BufferedImage local_icon) {
        super(name, url_icon, description, local_icon);
    }

    public static ArrayList<SuccessBadge> getSuccessBadges(final WebUserInfos user, final Member member_fetched) {
        ArrayList<SuccessBadge> badges = new ArrayList<>();
        if (UserInfosHandler.get(user.getId()).userDescription != null)
            badges.add(new Profile_Badge());
        return badges;
    }
}
