package devarea.bot.commands.inLine;

import devarea.bot.commands.SlashCommand;
import devarea.bot.presets.ColorsUsed;
import devarea.bot.commands.ShortCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Ask extends ShortCommand implements SlashCommand {
    public Ask(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        chatInteraction.reply("**Ne demande pas pour demander, demande !**").subscribe();
        send(MessageCreateSpec.builder()
                .addFile("image_ask.png", this.getClass().getResourceAsStream("/assets/image_ask.png"))
                .addEmbed(EmbedCreateSpec.builder()
                        .description("https://dontasktoask.com/")
                        .image("attachment://image_ask.png")
                        .color(ColorsUsed.same).build()
                ).build(), false);
        this.endCommand();
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
