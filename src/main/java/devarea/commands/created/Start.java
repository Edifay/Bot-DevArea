package devarea.commands.created;

import devarea.Data.ColorsUsed;
import devarea.Data.TextMessage;
import devarea.commands.FirstStape;
import devarea.commands.LongCommand;
import devarea.commands.Stape;
import devarea.event.EndStape;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;

import java.util.function.Consumer;

public class Start extends LongCommand {

    public Start(final MessageCreateEvent message) {
        super(message);

        Stape java = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                setText(embed -> {
                    embed.setTitle("Java");
                    embed.setDescription("Le java");
                    embed.setColor(ColorsUsed.just);
                });
                return end;
            }
        };

        this.firstStape = new FirstStape(this.channel, java) {
            @Override
            public void onFirstCall(Consumer<? super MessageCreateSpec> spec) {
                super.onFirstCall(msg -> msg.setEmbed(TextMessage.startCommandExplain));
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (content.equals("java"))
                    return callStape(0);
                else
                    sendErrorEntry();
                return next;
            }
        };
        this.lastMessage = this.firstStape.getMessage();
    }

}