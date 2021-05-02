package devarea.commands;

import devarea.Data.ColorsUsed;
import devarea.Main;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static devarea.Data.TextMessage.commandNotFound;

public class CommandManager {

    public static final Object key = new Object();

    private static final Map<String, Constructor> classBound = new HashMap<>();

    public static final Map<Snowflake, Command> actualCommands = new HashMap<>();

    public static void init() {
        try {

            ArrayList<String> names = getClassNamesFromPackage("devarea.commands.created");
            System.out.println("Class name found : " + Arrays.toString(names.toArray()));

            for (String className : names) {
                if (!className.contains("$")) {
                    final String newName = className.startsWith("/") ? className.substring(1) : className;
                    System.out.println(className + "->" + newName);
                    classBound.put(newName, Class.forName("devarea.commands.created." + newName).getConstructor(MessageCreateEvent.class));
                }
            }

        } catch (IOException | URISyntaxException | ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void exe(final String command, final MessageCreateEvent message) {
        synchronized (key) {
            final AtomicReference<Boolean> find = new AtomicReference<>(false);
            try {
                classBound.forEach((name, constructor) -> {
                    if (name.equalsIgnoreCase(command)) {
                        try {
                            System.out.println("The command " + name + " is executed !");
                            Command actualCommand = (Command) constructor.newInstance(message);
                            if (actualCommand instanceof LongCommand)
                                actualCommands.put(message.getMember().get().getId(), actualCommand);
                            find.set(true);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });

                if (!find.get())
                    Command.deletedEmbed((TextChannel) message.getMessage().getChannel().block(), embed -> {
                        embed.setTitle("Erreur !");
                        embed.setDescription(commandNotFound);
                        embed.setColor(ColorsUsed.wrong);
                    });

                if (Main.vanish)
                    try {
                        message.getMessage().delete().block();
                    } catch (Exception e) {
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

}