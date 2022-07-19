package devarea.bot.automatical;

import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.MemberCache;
import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.ConsumableCommand;
import devarea.bot.commands.inLine.GiveReward;
import devarea.bot.commands.commandTools.HelpReward;
import devarea.bot.utils.MemberUtil;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.Embed;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HelpRewardHandler {

    private static final TemporalAmount limitTime = Duration.ofHours(2);
    private static final ArrayList<HelpReward> helpRewards = new ArrayList<>();

    public static boolean react(ButtonInteractionEvent event) {

        System.out.println("Here ! React to the emoji !");

        if (event.getMessage().isEmpty() || event.getMessage().get().getEmbeds().isEmpty()) return false;

        final Message message = event.getMessage().get();
        final Embed embed = message.getEmbeds().get(0);

        if (embed.getDescription().isEmpty()) return false;

        final String description = embed.getDescription().get();
        final String pattern1 = " vous pourriez offrir une récompense à ";
        final String pattern2 = " pour son aide.";

        if (!description.contains(pattern1) || !description.contains(pattern2)) return false;

        final String[] members = description
                .replace(pattern1, "-")
                .replace(pattern2, "")
                .split("-");

        final Member target = MemberCache.get(MemberUtil.getSnowflakeByMentionText(members[0]).asString());
        final Member helper = MemberCache.get(MemberUtil.getSnowflakeByMentionText(members[1]).asString());

        System.out.println("Get target and helper");

        if (!event.getInteraction().getMember().get().equals(target)) {
            event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .ephemeral(true)
                    .addEmbed(EmbedCreateSpec.builder()
                            .color(ColorsUsed.wrong)
                            .title("Erreur !")
                            .description("Vous ne pouvez pas réagir à ce message !")
                            .build())
                    .build()).subscribe();
            return false;
        }

        CommandManager.addManualCommand(target, new ConsumableCommand(GiveReward.class) {
            @Override
            protected Command command() {
                return new GiveReward(event, this.member, helper);
            }
        });

        return true;
    }

    public static void addHelpReward(HelpReward helpReward) {
        helpRewards.add(helpReward);
    }

    public static void removeHelpReward(HelpReward helpReward) {
        helpRewards.remove(helpReward);
    }

    public static List<HelpReward> findHelpRewardsByMember(Member member) {
        List<HelpReward> findRewards = new ArrayList<>();
        List<HelpReward> deleteRewards = new ArrayList<>();
        for (final HelpReward reward : helpRewards) {
            if (reward.getDateTime().plus(limitTime).isBefore(LocalDateTime.now())) {
                deleteRewards.add(reward);
                continue;
            }

            if (reward.getMemberId().equals(member.getId().asString())
                    || reward.getHelpersIds().contains(member.getId().asString())) {
                findRewards.add(reward);
            }
        }

        helpRewards.removeAll(deleteRewards);

        return findRewards;
    }

    public static boolean canSendReward(Member member, List<Snowflake> snowflakes) {
        final List<HelpReward> rewards = findHelpRewardsByMember(member);
        final List<String> helperIds = snowflakes.stream().map(Snowflake::asString).collect(Collectors.toList());
        final String memberId = member.getId().asString();

        if (rewards.isEmpty()) return true;

        for (final HelpReward reward : rewards) {

            final String rMemberId = reward.getMemberId();
            final List<String> rHelperIds = reward.getHelpersIds();

            for (final String helperId : helperIds) {
                if (memberId.equals(rMemberId) && rHelperIds.contains(helperId)
                        || rHelperIds.contains(memberId) && rMemberId.equals(helperId)) {
                    return false;
                }
            }
        }
        return true;
    }

}
