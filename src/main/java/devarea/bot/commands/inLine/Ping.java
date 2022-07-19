package devarea.bot.commands.inLine;

import devarea.bot.commands.SlashCommand;
import devarea.bot.presets.ColorsUsed;
import devarea.bot.commands.ShortCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Ping extends ShortCommand implements SlashCommand {

    public Ping(final Member member, final ChatInputInteractionEvent event) {
        super(member, event);
        /*this.replyEmbed(EmbedCreateSpec.builder()
                .title("Pong !")
                .description("La latence avec le bot est de `" + (System.currentTimeMillis() - event.getInteraction()
                .getMessage().get().getTimestamp().toEpochMilli()) + "`ms.")
                .color(ColorsUsed.same).build(), false);*/
        this.replyEmbed(
                EmbedCreateSpec.builder()
                        .title("Work In Progress !")
                        .description("Nous attendons la mise à jour de la librairie !")
                        .color(ColorsUsed.same)
                        .build()
        );
        this.endCommand();
    }

    public Ping() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("ping")
                .description("Permet de tester la latence du bot !")
                .build();
    }
}
