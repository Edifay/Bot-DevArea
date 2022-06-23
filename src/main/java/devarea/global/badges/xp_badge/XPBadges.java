package devarea.global.badges.xp_badge;

import devarea.backend.controllers.tools.userInfos.WebUserInfos;
import devarea.global.badges.Badges;
import devarea.global.handlers.UserDataHandler;
import devarea.global.handlers.handlerData.UserData;
import discord4j.core.object.entity.Member;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class XPBadges extends Badges {
    public XPBadges(String name, String url_icon, String description, BufferedImage local_icon) {
        super(name, url_icon, description, local_icon);
    }

    public static XPBadges getXPBadgesOf(final WebUserInfos user, final Member member_fetched) {
        int xpLastWeekCount = getXpCountOnLastWeek(user, member_fetched);
        if (xpLastWeekCount > 150)
            System.out.println("Membre très actif !");
        else if (xpLastWeekCount > 100)
            System.out.println("Membre actif");
        else if (xpLastWeekCount > 50)
            System.out.println("Membre occasionné !");

        return null;
    }


    private static int getXpCountOnLastWeek(final WebUserInfos user, final Member member_fetched) {
        int count = 0;
        final UserData userData = UserDataHandler.get(user.getId());
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
