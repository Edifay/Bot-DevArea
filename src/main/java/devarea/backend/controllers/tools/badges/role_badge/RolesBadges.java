package devarea.backend.controllers.tools.badges.role_badge;

import devarea.backend.controllers.tools.badges.Badges;
import devarea.backend.controllers.tools.userInfos.WebUserInfos;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;

import java.util.ArrayList;
import java.util.Set;

public class RolesBadges extends Badges {
    // Staff
    public static final Snowflake fonda = Snowflake.of("768372172552667176");
    public static final Snowflake admin = Snowflake.of("768383784571240509");
    public static final Snowflake modo = Snowflake.of("777782222920744990");
    public static final Snowflake helper = Snowflake.of("777816365641760788");
    public static final Snowflake graphiste = Snowflake.of("840540882376851466");

    // Roles
    public static final Snowflake contest_winner = Snowflake.of("986924934959865876");

    public RolesBadges(String name, String url_icon, String description) {
        super(name, url_icon, description);
    }

    public static ArrayList<RolesBadges> getRolesBadges(final WebUserInfos user, final Member member_fetched) {
        ArrayList<RolesBadges> badges = new ArrayList<>();

        Set<Snowflake> roles = member_fetched.getRoleIds();

        // Staff
        if (roles.contains(fonda))
            badges.add(new Fonda_Badge());
        if (roles.contains(admin))
            badges.add(new Admin_Badge());
        if (roles.contains(modo))
            badges.add(new Modo_Badge());
        if (roles.contains(helper))
            badges.add(new Helper_Badge());
        if (roles.contains(graphiste))
            badges.add(new Graphiste_Badge());


        // Roles
        if (roles.contains(contest_winner))
            badges.add(new ContestWinner_Badge());


        return badges;
    }
}
