package devarea.commands;


import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;

import java.util.function.Consumer;

public abstract class FirstStape extends Stape {

    protected TextChannel textChannel;

    public FirstStape(TextChannel textChannel, Stape... stapes) {
        super(stapes);
        this.textChannel = textChannel;
        onFirstCall(null);
    }

    @Override
    @Deprecated
    protected boolean onCall(Message message) {
        return next;
    }

    public void onFirstCall(Consumer<? super MessageCreateSpec> spec) {
        this.message = this.textChannel.createMessage(spec).block();
    }

    public Message getMessage() {
        return this.message;
    }
}
