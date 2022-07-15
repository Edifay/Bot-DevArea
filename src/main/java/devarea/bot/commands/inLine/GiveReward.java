package devarea.bot.commands.inLine;

import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.automatical.HelpRewardHandler;
import devarea.global.handlers.XPHandler;
import devarea.bot.commands.EndStape;
import devarea.bot.commands.FirstStape;
import devarea.bot.commands.LongCommand;
import devarea.bot.commands.Stape;
import devarea.bot.commands.commandTools.HelpReward;
import devarea.bot.presets.ColorsUsed;
import devarea.bot.utils.MemberUtil;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;

import java.util.ArrayList;
import java.util.List;

public class GiveReward extends LongCommand {

    final List<Snowflake> helpers = new ArrayList<>();

    public GiveReward(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);

        if (!channel.getName().contains("entraide")) {
            this.sendError("Vous ne pouvez utiliser cette commande que dans les channels d'entraide");
            this.endCommand();
            return;
        }

        this.firstStape = getMessageCreateEventFirstStape(getEndStape(member));
        this.lastMessage = firstStape.getMessage();
    }

    public GiveReward(ReactionAddEvent event, Member target, Member helper) {
        super(target);
        final EndStape endStape = getEndStape(target);
        final Stape selectionStage = getSelectionHelpersStape(helper, endStape);
        channel = (TextChannel) ChannelCache.watch(event.getChannelId().asString());
        assert channel != null;

        delete(false, event.getMessage().block());
        this.firstStape = getReactionAddEventFirstStape(event, helper, endStape, selectionStage);
        this.lastMessage = firstStape.getMessage();
    }

    private FirstStape getMessageCreateEventFirstStape(Stape... stapes) {
        return new FirstStape(channel, stapes) {
            @Override
            public void onFirstCall(MessageCreateSpec spec) {
                super.onFirstCall(MessageCreateSpec.builder().addEmbed(EmbedCreateSpec.builder()
                        .title("Qui vous a aidé à résoudre votre problème ?")
                        .description("Veuillez mentionner les personnes dans votre prochain message.")
                        .color(ColorsUsed.same).build()).build());
            }

            @Override
            public boolean receiveMessage(MessageCreateEvent event) {
                final Message message = event.getMessage();
                final List<Snowflake> mentions = message.getUserMentionIds();

                if (mentions.isEmpty()) {
                    return super.receiveMessage(event);
                }

                if (!HelpRewardHandler.canSendReward(member, new ArrayList<>(mentions))) {

                    sendError(
                            "Vous avez déjà récompensé cette personne ou" +
                                    " il vous a déjà récompensé il y'a moins de deux heures"
                    );
                    return false;
                }

                for (final Snowflake mention : mentions) {

                    final Member mentionedMember = MemberCache.get(mention.asString());
                    assert mentionedMember != null;

                    if (mentionedMember.equals(member)) {
                        sendError("Veuillez mentionner une autre personne que vous même");
                        return false;
                    }
                }

                if (!HelpRewardHandler.canSendReward(member, new ArrayList<>(mentions))) {
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
            public void onFirstCall(MessageCreateSpec spec) {
                super.onFirstCall(MessageCreateSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Souhaitez-vous récompenser des personnes en plus ?")
                                .description(MemberUtil.getMentionTextByMember(member) + ", mettez <:ayy:" + ReactionEmoji.custom(Init.idNo).getId().asString() + "> si vous désirez uniquement récompenser " + MemberUtil.getMentionTextByMember(helper) + ".")
                                .color(ColorsUsed.just).build())
                        .addComponent(getYesNoButton())
                        .build());
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {

                int stapeIndex = -1;
                if (event.getCustomId().equals("yes")) stapeIndex = 1;
                if (event.getCustomId().equals("no")) stapeIndex = 0;

                if (stapeIndex > -1) {
                    helpers.add(helper.getId());
                    return callStape(stapeIndex);
                }

                return super.onReceiveInteract(event);
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
                    XPHandler.addXpToMember(MemberCache.get(helper.asString()), false, 50 / helpers.size());
                    tmpList.add(helper.asString());
                    tmpStr += MemberUtil.getMentionTextBySnowflake(helper) + " ";
                }

                XPHandler.removeXpToMember(member, 10);

                HelpRewardHandler.addHelpReward(new HelpReward(member.getId().asString(), tmpList));

                final String helpersText = tmpStr;
                final String authorMentionText = MemberUtil.getMentionTextByMember(member);
                final String description = helpers.size() > 1
                        ? "%s a récompensé %s qui l'ont aidé. Ils ont reçu " + (50 / helpers.size()) + " xp !"
                        : "%s a récompensé %s qui l'a aidé. Il a reçu 50 xp !";
                final String descript = String.format(description, authorMentionText, helpersText);

                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Dev'Area est heureux d'avoir pu servir à résoudre votre problème !")
                                .description(descript)
                                .color(ColorsUsed.just).build()
                        )
                        .components(getEmptyButton())
                        .build());
                return end;
            }
        };
    }

    private Stape getSelectionHelpersStape(Member helper, Stape... stapes) {

        return new Stape(stapes) {

            @Override
            protected boolean onCall(Message message) {
                this.setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Veuillez mentionner les personnes à ajouter")
                                .description(MemberUtil.getMentionTextByMember(member) + ", inutile de selectionner à" +
                                        " nouveau " + MemberUtil.getMentionTextByMember(helper))
                                .color(ColorsUsed.same).build())
                        .components(getEmptyButton())
                        .build());
                return next;
            }

            @Override
            public boolean onReceiveMessage(MessageCreateEvent event) {
                final Message message = event.getMessage();
                final List<Snowflake> mentions = message.getUserMentionIds();
                if (mentions.isEmpty() || mentions.contains(helper.getId())) {
                    return super.receiveMessage(event);
                }

                if (!HelpRewardHandler.canSendReward(member, new ArrayList<>(mentions))) {

                    sendError(
                            "Vous avez déjà récompensé cette personne ou" +
                                    " il vous a déjà récompensé il y'a moins de deux heures"
                    );
                    return false;
                }

                for (final Snowflake mention : mentions) {

                    final Member mentionedMember = MemberCache.get(mention.asString());
                    assert mentionedMember != null;

                    if (mentionedMember.equals(member)) {
                        sendError("Veuillez mentionner une autre personne que vous même");
                        return false;
                    }

                }
                if (!HelpRewardHandler.canSendReward(member, new ArrayList<>(mentions))) {
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
