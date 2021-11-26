package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.automatical.HelpRewardManager;
import devarea.bot.automatical.XpCount;
import devarea.bot.commands.EndStape;
import devarea.bot.commands.FirstStape;
import devarea.bot.commands.LongCommand;
import devarea.bot.commands.Stape;
import devarea.bot.commands.object_for_stock.HelpReward;
import devarea.bot.data.ColorsUsed;
import devarea.bot.utils.MemberUtil;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageCreateSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class GiveReward extends LongCommand {

    final List<Snowflake> helpers = new ArrayList<>();

    public GiveReward(MessageCreateEvent event) {
        super(event.getMessage().getAuthorAsMember().block());

        final Member author = event.getMember().get();
        channel = (TextChannel) event.getMessage().getChannel().block();
        assert channel != null;

        if (!channel.getName().contains("entraide")) {
            this.sendError("Vous ne pouvez utiliser cette commande que dans les channels d'entraide");
            this.endCommand();
            return;
        }

        this.firstStape = getMessageCreateEventFirstStape(event, getEndStape(author));
        this.lastMessage = firstStape.getMessage();
    }

    public GiveReward(ReactionAddEvent event, Member target, Member helper) {
        super(target);
        final EndStape endStape = getEndStape(target);
        final Stape selectionStage = getSelectionHelpersStape(helper, endStape);
        channel = (TextChannel) event.getChannel().block();
        assert channel != null;


        event.getMessage().block().delete().block();
        this.firstStape = getReactionAddEventFirstStape(event, helper, endStape, selectionStage);
        this.lastMessage = firstStape.getMessage();
    }

    private FirstStape getMessageCreateEventFirstStape(MessageCreateEvent event, Stape... stapes) {

        return new FirstStape(channel, stapes) {
            @Override
            public void onFirstCall(Consumer<? super MessageCreateSpec> spec) {
                super.onFirstCall(msg -> msg.setEmbed(embed -> {
                    embed.setTitle("Qui vous a aidé à résoudre votre problème ?");
                    embed.setDescription("Veuillez mentionner les personnes dans votre prochain message.");
                    embed.setColor(ColorsUsed.same);
                }));
            }

            @Override
            public boolean receiveMessage(MessageCreateEvent event) {
                final Message message = event.getMessage();
                final Set<Snowflake> mentions = message.getUserMentionIds();

                if (mentions.isEmpty()) {
                    return super.receiveMessage(event);
                }

                if(!HelpRewardManager.canSendReward(member, mentions.stream().toList())) {

                    sendError(
                            "Vous avez déjà récompensé cette personne ou" +
                                    " il vous a déjà récompensé il y'a moins de deux heures"
                    );
                    return false;
                }

                for (final Snowflake mention : mentions) {

                    final Member mentionedMember = Init.devarea.getMemberById(mention).block();
                    assert mentionedMember != null;

                    if (mentionedMember.equals(member)) {
                        sendError("Veuillez mentionner une autre personne que vous même");
                        return false;
                    }
                }

                if(!HelpRewardManager.canSendReward(member, mentions.stream().toList())) {
                    sendError(
                            "Vous avez déjà récompensé cette personne ou" +
                                    " il vous a déjà récompensé il y'a moins de deux heures"
                    );
                    return false;

                }

                helpers.addAll(mentions);

                return callStape(0);
            }
        };

    }

    private FirstStape getReactionAddEventFirstStape(ReactionAddEvent event, Member helper, Stape... stapes) {
        return new FirstStape(channel, stapes) {
            @Override
            public void onFirstCall(Consumer<? super MessageCreateSpec> spec) {
                super.onFirstCall(msg -> {
                    msg.setEmbed(embed -> {

                        final String descriptionText = "%s, mettez <:ayy:%s> si vous désirez uniquement récompenser %s.";
                        final String authorText = MemberUtil.getMentionTextByMember(member);
                        final String mentionText = MemberUtil.getMentionTextByMember(helper);
                        embed.setTitle("Souhaitez-vous récompenser des personnes en plus ?");
                        embed.setDescription("Veuillez mentionner les personnes dans votre prochain message.");
                        embed.setDescription(String.format(descriptionText, authorText, ReactionEmoji.custom(Init.idNo).getId().asString(), mentionText));

                        embed.setColor(ColorsUsed.just);
                    });
                });

                this.addYesEmoji();
                this.addNoEmoji();
            }

            @Override
            public boolean receiveReact(ReactionAddEvent event) {

                if (!event.getMember().get().equals(member))
                    event.getMessage().block().removeReaction(event.getEmoji(), event.getUserId()).block();

                int stapeIndex = -1;
                if (isYes(event)) stapeIndex = 1;
                if (isNo(event)) stapeIndex = 0;

                if (stapeIndex > -1) {
                    this.removeAllEmoji();
                    helpers.add(helper.getId());
                    return callStape(stapeIndex);
                }

                return super.receiveReact(event);
            }
        };
    }

    private EndStape getEndStape(Member member) {
        return new EndStape() {
            @Override
            protected boolean onCall(Message message) {

                List<String> tmpList = new ArrayList<>();
                String tmpStr = "";

                for (final Snowflake helper : helpers) {
                    XpCount.addXp(Init.devarea.getMemberById(helper).block(), 50 / helpers.size());
                    tmpList.add(helper.asString());
                    tmpStr += MemberUtil.getMentionTextBySnowflake(helper) + " ";
                }

                XpCount.removeXp(member, 10);

                HelpRewardManager.addHelpReward(new HelpReward(member.getId().asString(), tmpList));

                final String helpersText = tmpStr;
                final String authorMentionText = MemberUtil.getMentionTextByMember(member);
                final String description = helpers.size() > 1
                        ? "%s a récompensé %s qui l'ont aidé. Ils ont reçu " + (50 / helpers.size()) + " xp !"
                        : "%s a récompensé %s qui l'a aidé. Il a reçu 50 xp !";

                setMessage(spec -> {
                    spec.setEmbed(embed -> {
                        embed.setTitle("Dev'Area est heureux d'avoir pu servir à résoudre votre problème !");
                        embed.setDescription(String.format(description, authorMentionText, helpersText));
                        embed.setColor(ColorsUsed.just);
                    });
                });

                return end;
            }
        };
    }

    private Stape getSelectionHelpersStape(Member helper, Stape... stapes) {

        return new Stape(stapes) {

            @Override
            protected boolean onCall(Message message) {
                this.setMessage(spec -> {
                    spec.setEmbed(embed -> {
                        final String mentionText = MemberUtil.getMentionTextByMember(helper);
                        final String authorText = MemberUtil.getMentionTextByMember(member);

                        embed.setTitle("Veuillez mentionner les personnes à ajouter");
                        embed.setDescription(authorText + ", inutile de selectionner à nouveau " + mentionText);
                        embed.setColor(ColorsUsed.same);
                    });
                });

                return next;
            }

            @Override
            public boolean receiveMessage(MessageCreateEvent event) {
                final Message message = event.getMessage();
                final Set<Snowflake> mentions = message.getUserMentionIds();
                if (mentions.isEmpty() || mentions.contains(helper.getId())) {
                    return super.receiveMessage(event);
                }


                if(!HelpRewardManager.canSendReward(member, mentions.stream().toList())) {

                    sendError(
                            "Vous avez déjà récompensé cette personne ou" +
                                    " il vous a déjà récompensé il y'a moins de deux heures"
                    );
                    return false;
                }

                for (final Snowflake mention : mentions) {

                    final Member mentionedMember = Init.devarea.getMemberById(mention).block();
                    assert mentionedMember != null;

                    if (mentionedMember.equals(member)) {
                        sendError("Veuillez mentionner une autre personne que vous même");
                        return false;
                    }

                }
                if(!HelpRewardManager.canSendReward(member, mentions.stream().toList())) {
                    sendError(
                            "Vous avez déjà récompensé cette personne ou" +
                                    " il vous a déjà récompensé il y'a moins de deux heures"
                    );
                    return false;
                }

                helpers.addAll(mentions);

                return callStape(0);
            }
        };
    }

}
