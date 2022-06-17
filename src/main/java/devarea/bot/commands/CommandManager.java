package devarea.bot.commands;

import devarea.Main;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.PermissionSet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static devarea.bot.commands.Command.delete;
import static devarea.bot.presets.TextMessage.commandNotFound;

public class CommandManager {

    private static final Map<String, Constructor> classBound = new HashMap<>();

    private static final Map<Snowflake, LongCommand> actualCommands = new HashMap<>();

    private static final Map<Snowflake, Snowflake> logged_as = new HashMap<>();

    public static void init() {
        try {

            System.out.println(Main.separator + "Loading commands :");

            ArrayList<String> names = getClassNamesFromPackage("devarea.bot.commands.inLine");
            if (names.size() == 0) {
                names = getClassNamesFromPackage("BOOT-INF.classes.devarea.bot.commands.inLine");
            }
            System.out.println("Class name found : " + Arrays.toString(names.toArray()));

            for (String className : names) {
                if (!className.contains("$")) {
                    final String newName = className.startsWith("/") ? className.substring(1) : className;
                    System.out.print(className + "->" + newName + " | ");
                    if (Arrays.stream(Class.forName("devarea.bot.commands.inLine." + newName).getConstructors())
                            .anyMatch(constructor -> constructor.getGenericParameterTypes().length == 3
                                    && constructor.getGenericParameterTypes()[0].equals(Member.class)
                                    && constructor.getGenericParameterTypes()[1].equals(TextChannel.class)
                                    && constructor.getGenericParameterTypes()[2].equals(Message.class)))
                        classBound.put(newName.toLowerCase(Locale.ROOT), Class.forName("devarea.bot.commands.inLine" +
                                "." + newName).getConstructor(Member.class, TextChannel.class, Message.class));
                    else
                        System.err.println("\nImpossibilité de charger la commande : " + "devarea.bot.commands.inLine" +
                                "." + newName);

                }
            }

            System.out.println(classBound.size() + " commands loaded !");
        } catch (IOException | URISyntaxException | ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getClassNamesFromPackage(String packageName) throws IOException,
            URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL packageURL;
        ArrayList<String> names = new ArrayList<>();
        packageName = packageName.replace(".", "/");
        packageURL = classLoader.getResource(packageName);
        System.out.println(packageURL);
        if (packageURL.getProtocol().equals("jar")) {
            String jarFileName;
            JarFile jf;
            Enumeration<JarEntry> jarEntries;
            String entryName;

            jarFileName = URLDecoder.decode(packageURL.getFile(), StandardCharsets.UTF_8);
            jarFileName = jarFileName.substring(5, jarFileName.indexOf("!"));
            System.out.println(">" + jarFileName);
            jf = new JarFile(jarFileName);
            jarEntries = jf.entries();
            while (jarEntries.hasMoreElements()) {
                entryName = jarEntries.nextElement().getName();
                if (entryName.startsWith(packageName) && entryName.length() > packageName.length() + 5) {
                    entryName = entryName.substring(packageName.length(), entryName.lastIndexOf('.'));
                    names.add(entryName);
                }
            }
        } else {
            URI uri = new URI(packageURL.toString());
            File folder = new File(uri.getPath());
            File[] contenuti = folder.listFiles();
            String entryName;
            for (File actual : contenuti) {
                entryName = actual.getName();
                entryName = entryName.substring(0, entryName.lastIndexOf('.'));
                names.add(entryName);
            }
        }
        return names;
    }

    public static void exe(String command, final MessageCreateEvent message) {
        synchronized (actualCommands) {
            command = command.toLowerCase(Locale.ROOT);
            if (classBound.containsKey(command)) {
                try {
                    Member member_command = message.getMember().get();
                    Member member_replaced = logged_as.get(member_command.getId()) == null ? member_command :
                            MemberCache.get(logged_as.get(member_command.getId()).asString());
                    System.out.println("The command " + command + " is executed !");
                    PermissionSet permissionSet = null;
                    try {
                        PermissionCommand defaultCommand =
                                (PermissionCommand) classBound.get(command).getDeclaringClass().getConstructor(PermissionCommand.class).newInstance((PermissionCommand) () -> null);
                        permissionSet = defaultCommand.getPermissions();
                    } catch (NoSuchMethodException ignored) {
                    }

                    if (permissionSet == null || containPerm(permissionSet,
                            member_command.getBasePermissions().block())) {
                        Command actualCommand = (Command) classBound.get(command).newInstance(member_replaced,
                                ChannelCache.get(message.getMessage().getChannelId().asString()), message.getMessage());
                        if (actualCommand instanceof LongCommand)
                            actualCommands.put(member_replaced.getId(), (LongCommand) actualCommand);
                    } else
                        Command.sendError((TextChannel) ChannelCache.watch(message.getMessage().getChannelId().asString()), "Vous n'avez pas " +
                                "la permission d'éxécuter cette commande !");

                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else
                Command.deletedEmbed((TextChannel) ChannelCache.watch(message.getMessage().getChannelId().asString())
                        , EmbedCreateSpec.builder()
                        .title("Erreur !")
                        .description(commandNotFound)
                        .color(ColorsUsed.wrong).build());

            if (Init.initial.vanish)
                delete(false, message.getMessage());
        }

    }

    public static boolean addManualCommand(Member member, ConsumableCommand command) {
        return addManualCommand(member, command, false);
    }

    public static boolean addManualCommand(Member member, ConsumableCommand command, final boolean force_join) {
        synchronized (actualCommands) {
            try {
                Member member_command = member;
                Member member_replaced = logged_as.containsKey(member_command.getId()) ?
                        MemberCache.get(logged_as.get(member_command.getId()).asString()) : member_command;
                PermissionSet permissionSet = null;
                try {
                    PermissionCommand defaultCommand =
                            (PermissionCommand) command.commadClass.getConstructor(PermissionCommand.class).newInstance((PermissionCommand) () -> null);
                    permissionSet = defaultCommand.getPermissions();
                } catch (NoSuchMethodException e) {
                }
                if (permissionSet == null || containPerm(permissionSet, member_command.getBasePermissions().block())) {
                    if (!actualCommands.containsKey(member_replaced.getId()) || force_join) {
                        command.setMember(member_replaced);
                        if (command.getCommand(false) instanceof LongCommand)
                            actualCommands.put(member_replaced.getId(), (LongCommand) command.getCommand(true));
                    }
                    return true;
                }
                Command.sendError(command.channel, "Vous n'avez pas la permission d'éxécuter cette commande !");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static boolean react(ReactionAddEvent event) {
        synchronized (actualCommands) {
            Member member_replaced = logged_as.get(event.getUserId()) == null ? event.getMember().get() :
                    MemberCache.get(logged_as.get(event.getUserId()).asString());
            if (actualCommands.containsKey(member_replaced.getId())) {
                actualCommands.get(member_replaced.getId()).nextStape(event);
                return true;
            }
            return false;
        }
    }

    public static boolean receiveMessage(MessageCreateEvent event) {
        synchronized (actualCommands) {
            Member member_replaced = logged_as.get(event.getMessage().getAuthor().get().getId()) == null ?
                    event.getMember().get() :
                    MemberCache.get(logged_as.get(event.getMessage().getAuthor().get().getId()).asString());
            if (actualCommands.containsKey(member_replaced.getId())) {
                actualCommands.get(member_replaced.getId()).nextStape(event);
                return true;
            }
            return false;
        }
    }

    public static boolean receiveInteract(ButtonInteractionEvent event) {
        synchronized (actualCommands) {
            if (event.getInteraction().getMember().isEmpty()) return false;
            Member member_replaced = logged_as.get(event.getInteraction().getMember().get().getId()) == null ?
                    event.getInteraction().getMember().get() :
                    MemberCache.get(logged_as.get(event.getInteraction().getMember().get().getId()).asString());
            if (actualCommands.containsKey(member_replaced.getId())) {
                actualCommands.get(member_replaced.getId()).nextStape(event);
                return true;
            }
            return false;
        }
    }

    public static boolean hasCommand(Snowflake memberId) {
        synchronized (actualCommands) {
            return actualCommands.containsKey(memberId);
        }
    }

    public static boolean hasCommand(LongCommand command) {
        synchronized (actualCommands) {
            return actualCommands.containsValue(command);
        }
    }

    public static void removeCommand(Snowflake memberId) {
        synchronized (actualCommands) {
            actualCommands.remove(memberId);
        }
    }

    public static void removeCommand(Snowflake memberId, Command command) {
        synchronized (actualCommands) {
            if (actualCommands.containsValue(command))
                actualCommands.remove(memberId);
        }
    }

    public static void left(Snowflake id) {
        synchronized (actualCommands) {
            if (actualCommands.containsKey(id)) {
                actualCommands.get(id).endCommand();
            }
        }
    }

    public static int size() {
        synchronized (actualCommands) {
            return actualCommands.size();
        }
    }

    /*
        DON'T USE THIS METHOD !!!
     */
    @Deprecated
    public static Map<Snowflake, LongCommand> getMap() {
        synchronized (actualCommands) {
            return actualCommands;
        }
    }

    public static LongCommand getCommandOf(Snowflake id) {
        return actualCommands.get(id);
    }

    public static boolean containPerm(PermissionSet permissionToBeIn, PermissionSet PermissionsContainers) {
        AtomicBoolean atReturn = new AtomicBoolean(true);
        permissionToBeIn.stream().iterator().forEachRemaining(permission -> {
            AtomicBoolean haveFind = new AtomicBoolean(false);
            PermissionsContainers.stream().iterator().forEachRemaining(permission1 -> {
                if (permission.equals(permission1))
                    haveFind.set(true);
            });
            if (!haveFind.get())
                atReturn.set(false);
        });
        return atReturn.get();
    }

    public static void logAs(Snowflake memberToLog, Snowflake memberDestinationLog) {
        synchronized (logged_as) {
            logged_as.put(memberToLog, memberDestinationLog);
        }
    }

    public static void unLog(Snowflake memberToUnLog) {
        synchronized (logged_as) {
            logged_as.remove(memberToUnLog);
        }
    }

    public static Snowflake getLoggedAs(Snowflake member) {
        return logged_as.get(member);
    }

    public static Member getMemberLogged(Member member) {
        if (logged_as.containsKey(member))
            return MemberCache.get(logged_as.get(member.getId()).asString());
        else
            return member;
    }

}