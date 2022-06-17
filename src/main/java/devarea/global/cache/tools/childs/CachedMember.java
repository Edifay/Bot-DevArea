package devarea.global.cache.tools.childs;


import devarea.bot.Init;
import devarea.global.cache.MemberCache;
import devarea.global.cache.tools.CachedObject;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;

public class CachedMember extends CachedObject<Member> {

    public CachedMember(Member member, long last_fetch) {
        super(member, member.getId().asString(), last_fetch);
    }

    public CachedMember(final String memberID) {
        super(memberID);
    }

    @Override
    public Member fetch() {
        try {
            this.object_cached = Init.devarea.getMemberById(Snowflake.of(this.object_id)).block();
        } catch (Exception e) {
            System.err.println("ERROR: Member couldn't be fetched !");
            this.object_cached = null;
        }

        if (this.object_cached == null) {
            MemberCache.slash(this.object_id);
            return null;
        }

        this.last_fetch = System.currentTimeMillis();
        return this.object_cached;
    }
}
