package devarea.bot.commands.created;

import devarea.bot.data.ColorsUsed;
import devarea.bot.commands.ShortCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class Ask extends ShortCommand {
    public Ask(MessageCreateEvent message) {
        super(message);
        send(spec -> {
            spec.addFile("image_ask.png", this.getClass().getResourceAsStream("/assets/image_ask.png"));
            spec.setEmbed(embedCreateSpec -> {
                embedCreateSpec.setTitle("Ne demande pas pour demander, demande !");
                embedCreateSpec.setDescription("https://dontasktoask.com/");
                embedCreateSpec.setImage("attachment://image_ask.png");
                embedCreateSpec.setColor(ColorsUsed.same);
            });
        }, false);
        this.endCommand();
    }
}
