package devarea.bot.commands.inLine;

import devarea.bot.commands.CommandManager;
import devarea.bot.commands.LongCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.commands.SlashCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.time.Instant;
import java.util.Map;

public class InCommand extends ShortCommand implements SlashCommand {

    public InCommand() {
    }

    public InCommand(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        String text = "";
        if (CommandManager.size() > 0) {
            text += "Il y a actuellement " + CommandManager.size() + " commandes en cour :\n";
            for (Map.Entry<Snowflake, LongCommand> entry : CommandManager.getMap().entrySet()) {
                String[] names = entry.getValue().getClass().getName().split("\\.");
                text += "<@" + entry.getKey().asString() + "> : " + names[names.length - 1] + "\n";
            }
        } else
            text = "Il n'y a actuellement personne avec des commandes en cours.";

        this.replyEmbed(EmbedCreateSpec.builder()
                .title("Voici toutes les personnes ayant des commandes actives.").description(text)
                .color(ColorsUsed.same)
                .timestamp(Instant.now()).build(), false);
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("incommand")
                .description("Permet de savoir si des membres sont actuellement en LongCommand.")
                .build();
    }
}
