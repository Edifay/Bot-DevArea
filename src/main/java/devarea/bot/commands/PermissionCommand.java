package devarea.bot.commands;

import discord4j.rest.util.PermissionSet;

public interface PermissionCommand {

    PermissionSet getPermissions();
}
