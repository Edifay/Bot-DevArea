package devarea.global.badges;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.global.badges.role_badge.RolesBadges;
import devarea.global.badges.success_badge.SuccessBadge;
import devarea.backend.controllers.tools.userInfos.WebUserInfos;
import devarea.global.badges.time_badge.TimeOnServerBadge;
import devarea.global.badges.xp_badge.XPBadges;
import discord4j.core.object.entity.Member;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Badges {

    @JsonProperty("name")
    protected final String name;
    @JsonProperty("url_icon")
    protected final String url_icon;
    @JsonProperty
    protected final String description;
    @JsonIgnore
    protected final BufferedImage local_icon;

    public Badges(final String name, final String url_icon, final String description, final BufferedImage local_icon) {
        this.name = name;
        this.url_icon = url_icon;
        this.description = description;
        this.local_icon = local_icon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl_icon() {
        return url_icon;
    }

    public BufferedImage getLocal_icon() {
        return local_icon;
    }

    public static ArrayList<Badges> getBadgesOf(final WebUserInfos user, final Member member_fetched) {
        ArrayList<Badges> badges = new ArrayList<>(RolesBadges.getRolesBadges(user, member_fetched));
        badges.addAll(SuccessBadge.getSuccessBadges(user, member_fetched));
        badges.add(TimeOnServerBadge.getTimeBadgeOf(user, member_fetched));
        XPBadges.getXPBadgesOf(user, member_fetched);
        return badges;
    }

}
