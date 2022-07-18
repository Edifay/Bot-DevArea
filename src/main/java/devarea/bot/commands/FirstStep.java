package devarea.bot.commands;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;

public abstract class FirstStep extends Step {

    protected TextChannel textChannel;

    public FirstStep(TextChannel textChannel, Step... steps) {
        super(steps);
        this.textChannel = textChannel;
        onFirstCall(null);
    }

    @Override
    @Deprecated
    protected boolean onCall(Message message) {
        return next;
    }

    public void onFirstCall(MessageCreateSpec deleteThisVariableAndSetYourOwnMessage) {
        this.message = this.textChannel.createMessage(deleteThisVariableAndSetYourOwnMessage).block();
    }

    public Message getMessage() {
        return this.message;
    }
}
