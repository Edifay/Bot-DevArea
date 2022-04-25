package devarea.bot.cache.tools;


import devarea.bot.Init;
import devarea.bot.cache.MemberCache;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;

import java.util.Objects;

public class CachedMember {

    protected String memberID;
    protected Member member;
    protected long last_fetch;

    public CachedMember(final Member member, final long last_fetch) {
        this.member = member;
        this.memberID = member.getId().asString();
        this.last_fetch = last_fetch;
    }

    public CachedMember(final String memberID) {
        this.memberID = memberID;
        this.member = null;
        this.last_fetch = 0;
    }

    public Member get() {
        if (this.member == null || needToBeFetch()) {
            // System.out.println("Call fetch from get !" + needToBeFetch());
            return fetch();
        } else
            //     System.out.println("Cached : " + this.member.getUsername());
            return this.member;
    }

    protected boolean needToBeFetch() {
        return (System.currentTimeMillis() - this.last_fetch) > 600000;
    }

    public Member fetch() {
        this.member = Init.devarea.getMemberById(Snowflake.of(this.memberID)).block();
        if (this.member == null) {
            MemberCache.slash(this.memberID);
            return null;
        }
        this.last_fetch = System.currentTimeMillis();
        //     System.out.println("Fetch : " + this.member.getUsername());
        return this.member;
    }

    public Member watch() {
        //   System.out.println("Watch : " + this.member.getUsername());
        return this.member;
    }

    public void use(final Member member) throws Exception {
        //    System.out.println("Use : " + this.member.getUsername());
        if (this.memberID.equals(member.getId().asString())) {
            this.member = member;
            this.last_fetch = System.currentTimeMillis();
        } else
            throw new Exception("Wrong member usage !");
    }

    public void reset() {
        this.last_fetch = 0;
    }

    @Override
    public boolean equals(Object o) {
        return Objects.equals(memberID, o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberID, member, last_fetch);
    }
}
