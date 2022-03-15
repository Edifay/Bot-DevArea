package devarea.bot.automatical;

import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.ConsumableCommand;
import devarea.bot.commands.created.GiveReward;
import devarea.bot.commands.object_for_stock.HelpReward;
import devarea.bot.utils.MemberUtil;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.Embed;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HelpRewardManager {

    private static final TemporalAmount limitTime = Duration.ofHours(2);
    private static final ArrayList<HelpReward> helpRewards = new ArrayList<>();

    public static boolean react(ReactionAddEvent event) {
        final Message message = event.getMessage().block();

        if(message == null || message.getEmbeds().isEmpty()) return false;

        final Embed embed = message.getEmbeds().get(0);

        if(embed.getDescription().isEmpty()) return false;

        final String description = embed.getDescription().get();
        final String pattern1 = " vous pourriez offrir une récompense à ";
        final String pattern2 = " pour son aide.";

        if(!description.contains(pattern1) || !description.contains(pattern2)) return false;

        final String[] members = description
                .replace(pattern1, "-")
                .replace(pattern2, "")
                .split("-");

        final Member target = Init.devarea.getMemberById(MemberUtil.getSnowflakeByMentionText(members[0])).block();
        final Member helper = Init.devarea.getMemberById(MemberUtil.getSnowflakeByMentionText(members[1])).block();

        if(!event.getMember().get().equals(target)) {
            message.removeReaction(event.getEmoji(), event.getUserId()).block();
            return false;
        }

        if (!event.getEmoji().equals(ReactionEmoji.custom(Init.idYes))) return false;
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
        for(final HelpReward reward : helpRewards) {
            if(reward.getDateTime().plus(limitTime).isBefore(LocalDateTime.now())) {
                deleteRewards.add(reward);
                continue;
            }

            if(reward.getMemberId().equals(member.getId().asString())
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

        if(rewards.isEmpty()) return true;

        for(final HelpReward reward : rewards) {

            final String rMemberId = reward.getMemberId();
            final List<String> rHelperIds = reward.getHelpersIds();

            for(final String helperId : helperIds) {
                if(memberId.equals(rMemberId) && rHelperIds.contains(helperId)
                || rHelperIds.contains(memberId) && rMemberId.equals(helperId)) {
                    return false;
                }
            }
        }
        return true;
    }

}
