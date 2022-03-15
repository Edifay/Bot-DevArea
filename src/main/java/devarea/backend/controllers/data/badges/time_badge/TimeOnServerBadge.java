package devarea.backend.controllers.data.badges.time_badge;

import devarea.backend.controllers.data.UserInfo;
import devarea.backend.controllers.data.badges.Badges;
import discord4j.core.object.entity.Member;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeOnServerBadge extends Badges {

    public static final long millis_epoch_created = 1595894400000L;

    public TimeOnServerBadge(final String name, final String url, final String time_on_server) {
        super(name, url, "Vous etes un " + name + ", vous avez rejoin le serveur le " + time_on_server + ".");
    }


    public static Badges getTimeBadgeOf(final UserInfo user, final Member member_fetched) {
        Instant instant = member_fetched.getJoinTime().get();
        if (instant.isBefore(Instant.ofEpochMilli(millis_epoch_created).plus(90, ChronoUnit.DAYS))) {
            return new Precursor_Badge(instant.toString());
        } else if (instant.isBefore(Instant.now().minus(365, ChronoUnit.DAYS))) {
            return new Senior_Badge(instant.toString());
        } else {
            return new Junior_Badge(instant.toString());
        }
    }

}
