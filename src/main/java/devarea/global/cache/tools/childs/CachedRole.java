package devarea.global.cache.tools.childs;

import devarea.bot.Init;
import devarea.global.cache.MemberCache;
import devarea.global.cache.tools.CachedObject;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Role;

import java.util.Objects;

public class CachedRole extends CachedObject<Role> {

    public CachedRole(final Role role, final long last_fetch) {
        super(role, role.getId().asString(), last_fetch);
    }

    public CachedRole(final String roleID) {
        super(roleID);
    }


    @Override
    public Role fetch() {
        this.object_cached = Init.devarea.getRoleById(Snowflake.of(this.object_id)).block();
        return this.object_cached;
    }

    @Override
    public int hashCode() {
        return Objects.hash(object_id, object_cached, last_fetch);
    }

    public static int getRoleMemberCount(final String roleID) {
        int count = 0;

        for (CachedMember member : MemberCache.cache().values())
            if (member.watch() == null)
                System.err.println("ERROR: Member is null !");
            else if (member.watch().getRoleIds().contains(Snowflake.of(roleID)))
                count++;

        return count;
    }
}
