package devarea.bot.commands.inLine;

import devarea.bot.commands.SlashCommand;
import devarea.global.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.automatical.HelpRewardHandler;
import devarea.bot.commands.ShortCommand;
import devarea.bot.presets.ColorsUsed;
import devarea.bot.utils.MemberUtil;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.ArrayList;
import java.util.List;


public class AskReward extends ShortCommand implements SlashCommand {
    public AskReward() {
    }

    public AskReward(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);

        if (channel.getCategoryId().isEmpty() || !channel.getCategoryId().get().equals(Init.initial.assistance_category)) {
            this.replyError("Vous ne pouvez utiliser cette commande que dans les channels d'entraide");
            this.endCommand();
            return;
        }

        if (chatInteraction.getOption("mention").isEmpty() && chatInteraction.getOption("mention").get().getValue().isEmpty()) {
            this.replyError("Veuillez mentionner la personne que vous avez aidée");
            this.endCommand();
            return;
        }


        Member target =
                MemberCache.get(chatInteraction.getOption("mention").get().getValue().get().asSnowflake().asString());

        if (member.equals(target)) {
            this.replyError("Veuillez mentionner une autre personne que vous-même");
            this.endCommand();
            return;
        }

        final List<Snowflake> tmpList = new ArrayList<>();
        assert target != null;

        tmpList.add(target.getId());
        if (!(HelpRewardHandler.canSendReward(member, tmpList))) {
            this.replyError("Vous avez déjà récompensé cette personne ou il vous a déjà récompensé il y a moins de " +
                    "deux heures");
            this.endCommand();
            return;
        }

        final String authorMentionText = MemberUtil.getMentionTextByMember(member);
        final String targetMentionText = MemberUtil.getMentionTextByMember(target);
        final String descriptionText = "%s vous pourriez offrir une récompense à %s pour son aide.";


        reply(InteractionApplicationCommandCallbackSpec.builder()
                .addComponent(ActionRow.of(Button.primary("yes", ReactionEmoji.custom(Init.idYes))))
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Votre problème est-il résolu ?")
                        .description(String.format(descriptionText, targetMentionText, authorMentionText))
                        .color(ColorsUsed.same).build())
                .build());
        this.endCommand();
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("askreward")
                .description("Permet de faire une demande de reward à un membre du serveur !")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("mention")
                        .description("Mentionnez la personne que vous avez aidée.")
                        .type(ApplicationCommandOption.Type.MENTIONABLE.getValue())
                        .required(true)
                        .build())
                .build();
    }
}
