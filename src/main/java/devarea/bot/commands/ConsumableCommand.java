package devarea.bot.commands;

import discord4j.core.object.entity.channel.TextChannel;
import org.w3c.dom.Text;

public abstract class ConsumableCommand {
    protected Command command;
    public final Class commadClass;
    public final TextChannel channel;

    public ConsumableCommand(final TextChannel channel, final Class commandClass){
        this.channel = channel;
        this.commadClass = commandClass;
    }

    protected abstract Command command();

    public Command getCommand() {
        if (this.command == null) {
            this.command = command();
        }
        return this.command;
    }

}
