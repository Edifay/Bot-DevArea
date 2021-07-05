package devarea.bot.commands;

public abstract class ConsumableCommand {
    protected Command command;

    protected abstract Command command();

    public Command getCommand() {
        if (this.command == null) {
            this.command = command();
        }
        return this.command;
    }

}
