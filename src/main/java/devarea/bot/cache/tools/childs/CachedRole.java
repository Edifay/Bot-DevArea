package devarea.bot.cache.tools.childs;

import devarea.bot.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.cache.tools.CachedObject;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Role;

import java.util.Objects;

public class CachedRole extends CachedObject<Role> {

    protected String roleID;
    protected Role role;
    protected long last_fetch;

    public CachedRole(final Role role, final long last_fetch) {
        super(role, role.getId().asString(), last_fetch);
    }

    public CachedRole(final String roleID) {
        super(roleID);
    }


    @Override
    public Role fetch() {
        this.role = Init.devarea.getRoleById(Snowflake.of(this.roleID)).block();
        return this.role;
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
