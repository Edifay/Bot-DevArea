package devarea.bot.commands.inLine;

import devarea.bot.commands.SlashCommand;
import devarea.bot.presets.ColorsUsed;
import devarea.global.handlers.XPHandler;
import devarea.bot.commands.ShortCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class LeaderBoard extends ShortCommand implements SlashCommand {

    public LeaderBoard(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        String text = "";
        Snowflake[] array = XPHandler.getSortedMemberArray();
        for (int i = 0; i < 5; i++) {
            text += "`#" + (i + 1) + ":` <@" + array[i].asString() + ">: " + XPHandler.getXpOfMember(array[i]) + "xp" +
                    ".\n";
        }
        text += "\n---------------------------------------------------------------\n\n";
        text += "`#" + XPHandler.getRankOfMember(this.member.getId()) + ":` <@" + this.member.getId().asString() +
                ">: " + XPHandler.getXpOfMember(this.member.getId()) + "xp.";
        text += "\n\nVous pouvez retrouver le leaderboard en entier sur le site web : https://devarea.fr/stats.";
        replyEmbed(EmbedCreateSpec.builder()
                .title("LeaderBoard !")
                .color(ColorsUsed.same)
                .description(text).build(), false);
        this.endCommand();
    }

    public LeaderBoard() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("leaderboard")
                .description("Affiche le tableau du top 5 du classements d'XP du serveur.")
                .build();
    }
}
