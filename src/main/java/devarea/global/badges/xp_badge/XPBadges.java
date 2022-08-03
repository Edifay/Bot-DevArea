package devarea.global.badges.xp_badge;

import devarea.global.badges.Badges;
import devarea.global.handlers.UserDataHandler;
import devarea.global.handlers.handlerData.UserData;
import discord4j.core.object.entity.Member;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class XPBadges extends Badges {
    public XPBadges(String name, String url_icon, String description, BufferedImage local_icon) {
        super(name, url_icon, "Ce membre est un membre " + description + " sur le serveur !", local_icon);
    }

    public static XPBadges getXPBadgesOf(final Member member_fetched) {
        int xpLastWeekCount = getXpCountOnLastWeek(member_fetched);
        if (xpLastWeekCount > 100)
            return new ActiveMember_Badge();
        else if (xpLastWeekCount > 50)
            return new RegularMember_Badge();
        else if (xpLastWeekCount > 25)
            return new OccasionedMember_Badge();

        return null;
    }


    private static int getXpCountOnLastWeek(final Member member_fetched) {
        int count = 0;
        final UserData userData = UserDataHandler.get(member_fetched.getId().asString());
        LocalDateTime dateTime = LocalDateTime.now();


        for (int i = 0; i < 7; i++) {
            final String date = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH).format(dateTime);
            if (userData.xpGainHistory.containsKey(date))
                count += userData.xpGainHistory.get(date);
            dateTime = dateTime.minusDays(1L);
        }

        return count;
    }

}
