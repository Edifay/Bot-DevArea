package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.automatical.HelpRewardManager;
import devarea.bot.commands.ShortCommand;
import devarea.bot.data.ColorsUsed;
import devarea.bot.utils.MemberUtil;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class AskReward extends ShortCommand {
    public AskReward(final MessageCreateEvent event) {
        super(event);

        final Message message = event.getMessage();
        final Set<Snowflake> mentions = message.getUserMentionIds();


        if(!channel.getName().contains("entraide")) {
            this.sendError("Vous ne pouvez utiliser cette commande que dans les channels d'entraide");
            this.endCommand();
            return;
        }

        if(mentions.size() < 1) {
            this.sendError("Veuillez mentionner la personne que vous avez aidé");
            this.endCommand();
            return;
        }

        Snowflake firstMention = mentions.iterator().next();

        Member author = event.getMember().get();
        Member target = Init.devarea.getMemberById(firstMention).block();

        if(author.equals(target)) {
            this.sendError("Veuillez mentionner une autre personne que vous même");
            this.endCommand();
            return;
        }

        final List<Snowflake> tmpList = new ArrayList<>();
        assert target != null;

        tmpList.add(target.getId());
        if(!(HelpRewardManager.canSendReward(author, tmpList))) {
            this.sendError(
                "Vous avez déjà récompensé cette personne ou il vous a déjà récompensé il y'a moins de deux heures"
            );
            this.endCommand();
            return;
        }

        Message newMessage = this.sendEmbed(embed -> {
            final String authorMentionText = MemberUtil.getMentionTextByMember(author);
            final String targetMentionText = MemberUtil.getMentionTextByMember(target);
            final String descriptionText = "%s vous pourriez offrir une récompense à %s pour son aide.";

            embed.setTitle("Votre problème est-il résolu ?");
            embed.setDescription(String.format(descriptionText, targetMentionText, authorMentionText));
            embed.setColor(ColorsUsed.same);
        }, true);
        newMessage.addReaction(ReactionEmoji.custom(Init.idYes)).subscribe();
        this.endCommand();
    }
}
