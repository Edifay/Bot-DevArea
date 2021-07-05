package devarea.bot.commands;

import devarea.bot.Init;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.channel.TextChannel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static devarea.bot.commands.Command.delete;
import static devarea.bot.data.TextMessage.commandNotFound;

public class CommandManager {

    private static final Map<String, Constructor> classBound = new HashMap<>();

    private static final Map<Snowflake, LongCommand> actualCommands = new HashMap<>();

    public static void init() {
        try {

            ArrayList<String> names = getClassNamesFromPackage("devarea.bot.commands.created");
            if (names.size() == 0) {
                names = getClassNamesFromPackage("BOOT-INF.classes.devarea.bot.commands.created");
            }
            System.out.println("Class name found : " + Arrays.toString(names.toArray()));

            for (String className : names) {
                if (!className.contains("$")) {
                    final String newName = className.startsWith("/") ? className.substring(1) : className;
                    System.out.println(className + "->" + newName);
                    classBound.put(newName.toLowerCase(Locale.ROOT), Class.forName("devarea.bot.commands.created." + newName).getConstructor(MessageCreateEvent.class));
                }
            }

        } catch (IOException | URISyntaxException | ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getClassNamesFromPackage(String packageName) throws IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL packageURL;
        ArrayList<String> names = new ArrayList<>();
        packageName = packageName.replace(".", "/");
        packageURL = classLoader.getResource(packageName);
        if (packageURL.getProtocol().equals("jar")) {
            String jarFileName;
            JarFile jf;
            Enumeration<JarEntry> jarEntries;
            String entryName;

            jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
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
                    System.out.println("The command " + command + " is executed !");
                    Command actualCommand = (Command) classBound.get(command).newInstance(message);
                    if (actualCommand instanceof LongCommand)
                        actualCommands.put(message.getMember().get().getId(), (LongCommand) actualCommand);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else
                Command.deletedEmbed((TextChannel) message.getMessage().getChannel().block(), embed -> {
                    embed.setTitle("Erreur !");
                    embed.setDescription(commandNotFound);
                    embed.setColor(ColorsUsed.wrong);
                });

            if (Init.vanish)
                delete(false, message.getMessage());
        }

    }

    public static boolean addManualCommand(Snowflake memberId, ConsumableCommand command) {
        synchronized (actualCommands) {
            if (!actualCommands.containsKey(memberId) && command.getCommand() instanceof LongCommand) {
                actualCommands.put(memberId, (LongCommand) command.getCommand());
                return true;
            }
            return false;
        }
    }

    public static void react(ReactionAddEvent event) {
        synchronized (actualCommands) {
            if (actualCommands.containsKey(event.getUserId()))
                actualCommands.get(event.getUserId()).nextStape(event);
        }
    }

    public static boolean receiveMessage(MessageCreateEvent event) {
        synchronized (actualCommands) {
            if (actualCommands.containsKey(event.getMember().get().getId())) {
                actualCommands.get(event.getMember().get().getId()).nextStape(event);
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

}