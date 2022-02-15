package devarea.backend.controllers.data.badges;

import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.backend.controllers.data.UserInfo;
import devarea.backend.controllers.data.badges.time_badge.TimeOnServerBadge;
import discord4j.core.object.entity.Member;

import java.util.ArrayList;

public abstract class Badges {

    @JsonProperty("name")
    protected final String name;
    @JsonProperty("url_icon")
    protected final String url_icon;
    @JsonProperty
    protected final String description;

    public Badges(final String name, final String url_icon, final String description) {
        this.name = name;
        this.url_icon = url_icon;
        this.description = description;
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


    public static ArrayList<Badges> getBadgesOf(final UserInfo user, final Member member_fetched) {
        ArrayList<Badges> badges = new ArrayList<>();
        badges.add(TimeOnServerBadge.getTimeBadgeOf(user, member_fetched));
        return badges;
    }

}
