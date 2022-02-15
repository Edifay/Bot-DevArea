package devarea.bot.commands.created;

import devarea.bot.data.ColorsUsed;
import devarea.bot.commands.ShortCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

public class Ask extends ShortCommand {
    public Ask(MessageCreateEvent message) {
        super(message);
        send(MessageCreateSpec.builder()
                .addFile("image_ask.png", this.getClass().getResourceAsStream("/assets/image_ask.png"))
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Ne demande pas pour demander, demande !")
                        .description("https://dontasktoask.com/")
                        .image("attachment://image_ask.png")
                        .color(ColorsUsed.same).build()
                ).build(), false);
        this.endCommand();
    }
}
