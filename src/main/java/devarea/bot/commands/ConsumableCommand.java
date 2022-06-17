package devarea.bot.commands;

import devarea.global.cache.ChannelCache;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;

public abstract class ConsumableCommand {
    protected Command command;
    public Class commadClass;
    public TextChannel channel;
    public Member member;
    public Message message;

    public ConsumableCommand(final Class commandClass, final TextChannel channel) {
        this.commadClass = commandClass;
        this.channel = channel;
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

    public void setMessageEvent(Message message, Member member) {
        this.message = message;
        this.channel = (TextChannel) ChannelCache.get(message.getChannelId().asString());
        this.member = member;
    }
}
