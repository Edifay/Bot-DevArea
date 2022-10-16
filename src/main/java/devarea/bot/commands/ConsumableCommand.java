package devarea.bot.commands;

import devarea.global.cache.ChannelCache;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;

public abstract class ConsumableCommand {
    protected Command command;
    public Class commandClass;
    public GuildMessageChannel channel;
    public Member member;
    public Message message;
    public ChatInputInteractionEvent chatInteraction;

    public ConsumableCommand(final Class commandClass, final GuildMessageChannel channel) {
        this.commandClass = commandClass;
        this.channel = channel;
    }

    public ConsumableCommand(final Class commandClass) {
        this.commandClass = commandClass;
    }

    protected abstract Command command();

    public Command getCommand(final boolean wantReUse) {
        if (this.command == null || !wantReUse) {
            this.command = command();
        }
        return this.command;
    }

    public Command getCommand(final GuildMessageChannel channel) {
        this.channel = channel;
        if (this.command == null) {
            this.command = command();
        }
        return this.command;
    }

    public void setChannel(GuildMessageChannel channel) {
        this.channel = channel;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setMessageEvent(Message message, Member member) {
        this.message = message;
        this.channel = (GuildMessageChannel) ChannelCache.get(message.getChannelId().asString());
        this.member = member;
    }

    public void setChatInteraction(ChatInputInteractionEvent chatInteraction) {
        this.chatInteraction = chatInteraction;
    }
}
