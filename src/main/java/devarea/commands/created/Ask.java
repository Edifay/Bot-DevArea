package devarea.commands.created;

import devarea.data.ColorsUsed;
import devarea.commands.ShortCommand;
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
        });
        this.endCommand();
    }
}
