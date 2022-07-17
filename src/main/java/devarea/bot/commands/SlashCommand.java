package devarea.bot.commands;

import discord4j.discordjson.json.ApplicationCommandRequest;

public interface SlashCommand {
    /*
        Implement the definition of the command for generate slash Command
     */
    ApplicationCommandRequest getSlashCommandDefinition();
}
