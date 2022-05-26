package devarea.bot.cache.tools;

import devarea.bot.cache.MemberCache;
import devarea.bot.Init;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Role;

import java.util.Objects;

public class CachedRole {

    protected String roleID;
    protected Role role;
    protected long last_fetch;

    public CachedRole(final Role role, final long last_fetch) {
        this.role = role;
        this.roleID = role.getId().asString();
        this.last_fetch = last_fetch;
    }

    public CachedRole(final String roleID) {
        this.role = null;
        this.roleID = roleID;
        this.last_fetch = 0;
    }

    public Role get() {
        if (this.role == null || needToBeFetch())
            return fetch();
        return this.role;
    }

    protected boolean needToBeFetch() {
        return (System.currentTimeMillis() - this.last_fetch) > 600000;
    }

    public Role fetch() {
        this.role = Init.devarea.getRoleById(Snowflake.of(this.roleID)).block();
        return this.role;
    }

    public Role watch() {
        return this.role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return Objects.equals(roleID, o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleID, role, last_fetch);
    }


    public static int getRoleMemberCount(final String roleID) {
        int count = 0;

        for (CachedMember member : MemberCache.cache().values())
            if (member.watch() == null)
                System.err.println("ERROR !!!!!!!!!!!!! -> Le membre est null !");
            else if (member.watch().getRoleIds().contains(Snowflake.of(roleID)))
                count++;

        return count;
    }
}
