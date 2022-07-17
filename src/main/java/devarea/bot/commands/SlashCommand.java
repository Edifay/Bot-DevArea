package devarea.bot.commands;

import discord4j.discordjson.json.ApplicationCommandRequest;

public interface SlashCommand {
    ApplicationCommandRequest getSlashCommandDefinition();
}
