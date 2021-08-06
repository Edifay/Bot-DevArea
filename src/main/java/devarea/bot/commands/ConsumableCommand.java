package devarea.bot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;

public abstract class ConsumableCommand {
    protected Command command;
    public Class commadClass;
    public TextChannel channel;
    public Member member;
    public MessageCreateEvent messageEvent;

    public ConsumableCommand(final TextChannel channel, final Class commandClass) {
        this.channel = channel;
        this.commadClass = commandClass;
    }

    public ConsumableCommand(final Class commandClass) {
        this.commadClass = commandClass;
    }

    protected abstract Command command();

    public Command getCommand(final boolean wantReUse) {
        if (this.command == null || !wantReUse) {
            this.command = command();
        }
        return this.command;
    }

    public Command getCommand(final TextChannel channel) {
        this.channel = channel;
        if (this.command == null) {
            this.command = command();
        }
        return this.command;
    }

    public void setChannel(TextChannel channel) {
        this.channel = channel;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setMessageEvent(MessageCreateEvent messageEvent) {
        this.messageEvent = messageEvent;
        this.channel = (TextChannel) messageEvent.getMessage().getChannel().block();
        this.member = messageEvent.getMember().get();
    }
}
