package devarea.bot.commands.inLine;

import devarea.bot.commands.SlashCommand;
import devarea.bot.presets.ColorsUsed;
import devarea.bot.commands.ShortCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Ask extends ShortCommand implements SlashCommand {
    public Ask(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        chatInteraction.reply(InteractionApplicationCommandCallbackSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Ne demande pas pour demander, demande !")
                        .description("https://dontasktoask.com/")
                        .image("https://devarea.fr/assets/images/image_ask.png")
                        .color(ColorsUsed.same).build()
                ).build()).subscribe();
    }

    public Ask() {

    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("ask")
                .description("Explique comment bien poser une question.")
                .build();
    }
}
