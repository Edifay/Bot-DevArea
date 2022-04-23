package devarea.bot.commands.inLine;

import devarea.bot.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.automatical.HelpRewardHandler;
import devarea.bot.commands.ShortCommand;
import devarea.bot.presets.ColorsUsed;
import devarea.bot.utils.MemberUtil;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;

import java.util.ArrayList;
import java.util.List;


public class AskReward extends ShortCommand {
    public AskReward(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);

        List<User> mentions = message.getUserMentions();


        if (!channel.getName().contains("entraide")) {
            this.sendError("Vous ne pouvez utiliser cette commande que dans les channels d'entraide");
            this.endCommand();
            return;
        }

        if (mentions.size() < 1) {
            this.sendError("Veuillez mentionner la personne que vous avez aidé");
            this.endCommand();
            return;
        }

        Snowflake firstMention = mentions.get(0).getId();

        Member target = MemberCache.get(firstMention.asString());

        if (member.equals(target)) {
            this.sendError("Veuillez mentionner une autre personne que vous même");
            this.endCommand();
            return;
        }

        final List<Snowflake> tmpList = new ArrayList<>();
        assert target != null;

        tmpList.add(target.getId());
        if (!(HelpRewardHandler.canSendReward(member, tmpList))) {
            this.sendError(
                    "Vous avez déjà récompensé cette personne ou il vous a déjà récompensé il y'a moins de deux heures"
            );
            this.endCommand();
            return;
        }

        final String authorMentionText = MemberUtil.getMentionTextByMember(member);
        final String targetMentionText = MemberUtil.getMentionTextByMember(target);
        final String descriptionText = "%s vous pourriez offrir une récompense à %s pour son aide.";

        Message newMessage = this.sendEmbed(
                EmbedCreateSpec.builder()
                        .title("Votre problème est-il résolu ?")
                        .description(String.format(descriptionText, targetMentionText, authorMentionText))
                        .color(ColorsUsed.same).build(), true);
        newMessage.addReaction(ReactionEmoji.custom(Init.idYes)).subscribe();
        this.endCommand();
    }
}
