package devarea.bot.commands;

import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.Set;

public interface PermissionCommand {

    PermissionSet getPermissions();
}
