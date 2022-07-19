package devarea.bot.commands.inLine;

import devarea.bot.commands.*;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.automatical.HelpRewardHandler;
import devarea.global.handlers.XPHandler;
import devarea.bot.commands.commandTools.HelpReward;
import devarea.bot.presets.ColorsUsed;
import devarea.bot.utils.MemberUtil;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.*;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.ArrayList;
import java.util.List;

import static devarea.global.utils.ThreadHandler.startAwayIn;

public class GiveReward extends LongCommand implements SlashCommand {

    final List<Snowflake> helpers = new ArrayList<>();

    public GiveReward(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);

        if (channel.getCategoryId().isEmpty() || !channel.getCategoryId().get().equals(Init.initial.assistance_category)) {
            this.replyError("Vous ne pouvez utiliser cette commande que dans les channels d'entraide");
            this.endCommand();
            return;
        }

        this.firstStep = getMessageCreateEventFirstStep(getEndStep(member));
        this.lastMessage = firstStep.getMessage();
    }

    public GiveReward(ButtonInteractionEvent event, Member target, Member helper) {
        super(target);
        final EndStep endStep = getEndStep(target);
        final Step selectionStage = getSelectionHelpersStep(helper, endStep);
        channel = (TextChannel) ChannelCache.watch(event.getInteraction().getChannelId().asString());
        assert channel != null;

        delete(false, event.getInteraction().getMessage().get());
        this.firstStep = getReactionAddEventFirstStep(helper, endStep, selectionStage);
        this.lastMessage = firstStep.getMessage();
    }

    private FirstStep getMessageCreateEventFirstStep(Step... steps) {
        return new FirstStep(channel, steps) {
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
                    chatInteraction.editReply(InteractionReplyEditSpec.builder()
                            .addEmbed(EmbedCreateSpec.builder()
                                    .title("Erreur !")
                                    .color(ColorsUsed.wrong)
                                    .description("Vous avez déjà récompensé cette personne ou" +
                                            " elle vous a déjà récompensé il y a moins de deux heures")
                                    .build())
                            .build()).subscribe();
                    return true;
                }

                for (final Snowflake mention : mentions) {

                    final Member mentionedMember = MemberCache.get(mention.asString());
                    assert mentionedMember != null;

                    if (mentionedMember.equals(member)) {
                        sendError("Veuillez mentionner une autre personne que vous-même");
                        return false;
                    }
                }

                if (!HelpRewardHandler.canSendReward(member, new ArrayList<>(mentions))) {
                    sendError(
                            "Vous avez déjà récompensé cette personne ou" +
                                    " elle vous a déjà récompensé il y a moins de deux heures"
                    );
                    return false;
                }
                helpers.addAll(mentions);
                return callStep(0);
            }
        };

    }

    private FirstStep getReactionAddEventFirstStep(Member helper, Step... steps) {
        return new FirstStep(channel, steps) {
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

                int stepIndex = -1;
                if (event.getCustomId().equals("yes")) stepIndex = 1;
                if (event.getCustomId().equals("no")) stepIndex = 0;

                if (stepIndex > -1) {
                    helpers.add(helper.getId());
                    return callStep(stepIndex);
                }

                return super.onReceiveInteract(event);
            }
        };
    }

    private EndStep getEndStep(Member member) {
        return new EndStep() {
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

                if (chatInteraction != null) {
                    chatInteraction.editReply(InteractionReplyEditSpec.builder()
                            .addEmbed(EmbedCreateSpec.builder()
                                    .title("Dev'Area est heureux d'avoir pu servir à résoudre votre problème !")
                                    .description(descript)
                                    .color(ColorsUsed.just).build())
                            .build()).subscribe();
                } else
                    setMessage(MessageEditSpec.builder()
                            .addEmbed(EmbedCreateSpec.builder()
                                    .title("Dev'Area est heureux d'avoir pu servir à résoudre votre problème !")
                                    .description(descript)
                                    .color(ColorsUsed.just).build())
                            .components(getEmptyButton())
                            .build());
                return end;
            }
        };
    }

    private Step getSelectionHelpersStep(Member helper, Step... steps) {

        return new Step(steps) {

            @Override
            protected boolean onCall(Message message) {
                this.setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Veuillez mentionner les personnes à ajouter")
                                .description(MemberUtil.getMentionTextByMember(member) + ", inutile de sélectionner à" +
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
                    sendError("Veillez mentionner quelqu'un d'autre !");
                    return false;
                }

                if (!HelpRewardHandler.canSendReward(member, new ArrayList<>(mentions))) {

                    sendError(
                            "Vous avez déjà récompensé cette personne ou" +
                                    " elle vous a déjà récompensé il y a moins de deux heures"
                    );
                    return false;
                }

                for (final Snowflake mention : mentions) {

                    final Member mentionedMember = MemberCache.get(mention.asString());
                    assert mentionedMember != null;

                    if (mentionedMember.equals(member)) {
                        sendError("Veuillez mentionner une autre personne que vous-même");
                        return false;
                    }

                }
                if (!HelpRewardHandler.canSendReward(member, new ArrayList<>(mentions))) {
                    sendError(
                            "Vous avez déjà récompensé cette personne ou" +
                                    " elle vous a déjà récompensé il y a moins de deux heures"
                    );
                    return false;
                }

                helpers.addAll(mentions);

                return callStep(0);
            }
        };
    }

    public GiveReward() {

    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("givereward")
                .description("Permet de donner une petite récompense aux personnes qui vous ont aidé !")
                .build();
    }
}
