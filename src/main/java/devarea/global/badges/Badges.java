package devarea.global.badges;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.global.badges.role_badge.RolesBadges;
import devarea.global.badges.success_badge.SuccessBadge;
import devarea.backend.controllers.tools.userInfos.WebUserInfos;
import devarea.global.badges.time_badge.TimeOnServerBadge;
import devarea.global.badges.xp_badge.XPBadges;
import discord4j.core.object.entity.Member;
import reactor.util.annotation.Nullable;

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

    /*
        Check every badges types and return all the bages own by the user -> (member_fetched)
     */
    public static ArrayList<Badges> getBadgesOf(final Member member_fetched) {
        ArrayList<Badges> badges = new ArrayList<>(RolesBadges.getRolesBadges(member_fetched));
        Badges xpBadge;
        if ((xpBadge = XPBadges.getXPBadgesOf(member_fetched)) != null)
            badges.add(xpBadge);
        badges.addAll(SuccessBadge.getSuccessBadges(member_fetched));
        badges.add(TimeOnServerBadge.getTimeBadgeOf(member_fetched));
        return badges;
    }

}
