package devarea.backend.controllers.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.bot.automatical.FreeLanceHandler;
import devarea.bot.cache.MemberCache;
import devarea.bot.commands.commandTools.FreeLance;
import discord4j.core.object.entity.Member;

import java.util.ArrayList;

public class WebFreelance {

    @JsonProperty
    protected String member_id;
    @JsonProperty
    protected String name;
    @JsonProperty
    protected String avatar_url;
    @JsonProperty
    protected String description;
    @JsonProperty
    protected ArrayList<FreeLance.FieldSeria> fields;

    public WebFreelance(FreeLance freelance) {
        this.member_id = freelance.getMemberId();
        this.name = freelance.getFreeLanceName();

        Member member = MemberCache.get(freelance.getMemberId());
        if (member != null)
            this.avatar_url = member.getAvatarUrl();
        else
            FreeLanceHandler.remove(freelance);

        this.description = freelance.getDescription();

        this.fields = freelance.getFields();
    }

    public static class WebFreelancePreview {
        @JsonProperty
        protected String member_id;
        @JsonProperty
        protected String name;
        @JsonProperty
        protected String avatar_url;
        @JsonProperty
        protected String description;
        @JsonProperty
        protected String[] abilities;

        public WebFreelancePreview(final FreeLance freelance) {
            this.member_id = freelance.getMemberId();
            this.name = freelance.getFreeLanceName();

            Member member = MemberCache.get(freelance.getMemberId());
            if (member != null)
                this.avatar_url = member.getAvatarUrl();
            else
                FreeLanceHandler.remove(freelance);

            this.description = freelance.getDescription().length() > 150 ? freelance.getDescription().substring(0,
                    150) :
                    freelance.getDescription();

            ArrayList<String> abilities = new ArrayList<>();
            for (FreeLance.FieldSeria field : freelance.getFields())
                if (!field.getTitle().equals("Contact") && !field.getTitle().equals("Liens"))
                    abilities.add(field.getTitle());
            this.abilities = abilities.toArray(new String[0]);
        }
    }
}
