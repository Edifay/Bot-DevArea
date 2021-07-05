package devarea.bot.commands;

import discord4j.rest.util.Permission;

import java.util.Set;

public interface PermissionCommand {
    Set<Permission> getPermissions();
}
