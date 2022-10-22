package devarea.bot.commands;

import devarea.Main;
import devarea.bot.Init;
import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.PermissionSet;
import reactor.util.annotation.Nullable;

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
import static devarea.bot.commands.Command.sendError;
import static devarea.bot.presets.TextMessage.commandNotFound;

public class CommandManager {

    // Command name bound to the Constructor
    private static final Map<String, Constructor> classBound = new HashMap<>();

    // Member ID bound to the alive Command
    private static final Map<Snowflake, LongCommand> currentCommands = new HashMap<>();

    // Member ID bound to another Member ID
    private static final Map<Snowflake, Snowflake> logged_as = new HashMap<>();

    public static void init() {
        try {
            System.out.println("Loading commands :");

            ArrayList<String> names = getClassNamesFromAnySources();

            System.out.println("Class names found : " + Arrays.toString(names.toArray()));

            List<ApplicationCommandRequest> slashCommands = new ArrayList<>();


            // Setup Commands

            for (String className : names) {

                if (className.contains("$")) // Don't bound anonymous or statics classes.
                    continue;

                // refactor the ClassName for being used
                final String patchedName = className.startsWith("/") ? className.substring(1) : className;
                System.out.print(className + "->" + patchedName + " | ");

                // Loading Class in inLine package
                Class<?> currentClass = Class.forName("devarea.bot.commands.inLine." + patchedName);
                Constructor<?> constructor = null;

                // Setup Constructor
                if (isMessageCommandConstructor(currentClass)) {
                    constructor = currentClass.getConstructor(Member.class, GuildMessageChannel.class, Message.class);
                } else if (isSlashCommandConstructor(currentClass)) {
                    constructor = currentClass.getConstructor(Member.class, ChatInputInteractionEvent.class);

                    // If is slash command add it to the collection !
                    if (Arrays.asList(currentClass.getInterfaces()).contains(SlashCommand.class))
                        slashCommands.add(((SlashCommand) currentClass.getConstructor().newInstance()).getSlashCommandDefinition());
                }

                // Bounding constructor to the className
                if (constructor != null)
                    classBound.put(patchedName.toLowerCase(), constructor);
                else
                    System.err.println("\nImpossibilité de charger la commande : " + "devarea.bot.commands" +
                            ".inLine" + "." + patchedName);

            }

            System.out.println();

            // update Bot SlashCommands Bot Application
            Init.client.getRestClient().getApplicationService()
                    .bulkOverwriteGlobalApplicationCommand(Init.client.getRestClient().getApplicationId().block(),
                            slashCommands).subscribe();

            System.out.println(slashCommands.size() + " slash commands on " + classBound.size() + " commands loaded " + "!\n" + Main.separator);


        } catch (NoSuchMethodException e) {
            System.err.println("The SlashCommand need an empty constructor to be initialized !\n\n" + e.getMessage());
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                 ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
        Return the list of classes in inLine Package.
        Check if the path is good, 2 different loading type : in IDE dynamic load, and in jar POT.
     */
    private static ArrayList<String> getClassNamesFromAnySources() {
        ArrayList<String> names;
        try {
            names = getClassNamesFromPackage("devarea.bot.commands.inLine");
            if (names.size() == 0) names = getClassNamesFromPackage("BOOT-INF.classes.devarea.bot.commands.inLine");
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return names;
    }

    /*
        Check if the class (param) contain a SlashCommand Constructor
     */
    private static boolean isSlashCommandConstructor(Class<?> currentClass) {
        return Arrays.stream(currentClass.getConstructors()).anyMatch(constructor -> constructor.getGenericParameterTypes().length == 2 && constructor.getGenericParameterTypes()[0].equals(Member.class) && constructor.getGenericParameterTypes()[1].equals(ChatInputInteractionEvent.class));
    }

    /*
        Check if the class (param) contain a MessageCommand Constructor
     */
    private static boolean isMessageCommandConstructor(Class<?> currentClass) {
        return Arrays.stream(currentClass.getConstructors()).anyMatch(constructor -> constructor.getGenericParameterTypes().length == 3 && constructor.getGenericParameterTypes()[0].equals(Member.class) && constructor.getGenericParameterTypes()[1].equals(GuildMessageChannel.class) && constructor.getGenericParameterTypes()[2].equals(Message.class));
    }

    /*
        Execute a String command to a user from events.
        Configuration to exe :
         -> message != null and chatInteraction == null
         -> message == null and chatInteraction != null
     */
    public static void exe(String command, @Nullable final MessageCreateEvent message,
                           @Nullable final ChatInputInteractionEvent chatInteraction) {
        command = command.toLowerCase();

        if (classBound.containsKey(command)) {
            System.out.println("The command " + command + " is executed !");

            try {

                Member member_execute = getExecuteMember(message, chatInteraction);
                Member member_logged = getMemberIdentity(member_execute);

                if (havePermissionToExecute(classBound.get(command).getDeclaringClass(), member_execute)) {
                    Command executedCommand = null;

                    if (message != null) { // if the event is a MessageCreateEvent

                        if (classBound.get(command).getParameterCount() == 2) { // If constructor is SlashCommand
                            sendError((GuildMessageChannel) ChannelCache.get(message.getMessage().getChannelId().asString()),
                                    "Cette commande à migré vers les ``slash`` commandes !");
                        } else // If the constructor is MessageCreateEvent
                            // Creating command for MessageCommands
                            executedCommand = (Command) classBound.get(command).newInstance(member_logged,
                                    ChannelCache.get(message.getMessage().getChannelId().asString()),
                                    message.getMessage());
                    }

                    if (chatInteraction != null && executedCommand == null) // if the event is a ChatInteraction
                        // Creating command for SlashCommands
                        executedCommand = (Command) classBound.get(command).newInstance(member_logged, chatInteraction);


                    if (executedCommand == null) {
                        sendError((GuildMessageChannel) ChannelCache.get(message.getMessage().getChannelId().asString()),
                                "Aucun constructeur de commande n'a été trouvé pour votre appel de commande !");
                        return;
                    }

                    // If the executed command is LongCommand set up the mission Follow
                    if (executedCommand instanceof LongCommand)
                        currentCommands.put(member_logged.getId(), (LongCommand) executedCommand);

                } else { // Don't have perm to exe the command
                    if (message != null)
                        Command.sendError((GuildMessageChannel) ChannelCache.watch(message.getMessage().getChannelId().asString()), "Vous n'avez pas " + "la permission d'exécuter cette commande !");
                    else
                        chatInteraction.reply(InteractionApplicationCommandCallbackSpec.builder().ephemeral(true).addEmbed(EmbedCreateSpec.builder().color(ColorsUsed.wrong).title("Erreur !").description("Vous n'avez pas la permission d'éxécuter cette commande !").build()).build()).subscribe();
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        } else
            Command.deletedEmbed((GuildMessageChannel) ChannelCache.watch(message.getMessage().getChannelId().asString()),
                    EmbedCreateSpec.builder().title("Erreur !").description(commandNotFound).color(ColorsUsed.wrong).build());

        if (Init.initial.vanish) delete(false, message.getMessage());

    }

    private static boolean havePermissionToExecute(Class<?> command, Member member_execute) {
        try {
            try {
                PermissionCommand defaultCommand =
                        (PermissionCommand) command.getConstructor(PermissionCommand.class).newInstance((PermissionCommand) () -> null);
                PermissionSet permissionSet = defaultCommand.getPermissions();

                return containPerm(permissionSet, member_execute.getBasePermissions().block());
            } catch (NoSuchMethodException ignored) {
                // No perm needed
                return true;
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            // Couldn't load perms !
            return false;
        }
    }

    private static Member getExecuteMember(MessageCreateEvent message, ChatInputInteractionEvent chatInteraction) {
        return message != null ? message.getMember().get() :
                chatInteraction.getInteraction().getMember().get();
    }



    public static boolean addManualCommand(Member member, ConsumableCommand command) {
        return addManualCommand(member, command, false);
    }

    // Add a command with out event with ConsumableCommand.
    public static boolean addManualCommand(Member member, ConsumableCommand command, final boolean force_join) {
        try {
            Member member_replaced = getMemberIdentity(member);
            if (havePermissionToExecute(command.commandClass, member)) {

                if (!currentCommands.containsKey(member_replaced.getId()) || force_join) {

                    command.setMember(member_replaced);
                    if (command.getCommand(false) instanceof LongCommand)
                        currentCommands.put(member_replaced.getId(), (LongCommand) command.getCommand(true));

                }

                return true;
            }

            Command.sendError(command.channel, "Vous n'avez pas la permission d'exécuter cette commande !");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // receive a reaction and dispatch it to LongCommand
    public static boolean react(ReactionAddEvent event) {
        Member member_replaced = getMemberIdentity(event.getMember().get());
        if (currentCommands.containsKey(member_replaced.getId())) {
            currentCommands.get(member_replaced.getId()).nextStep(event);
            return true;
        }
        return false;
    }

    // receive a message and dispatch it to LongCommand
    public static boolean receiveMessage(MessageCreateEvent event) {
        Member member_replaced = getMemberIdentity(event.getMember().get());
        if (currentCommands.containsKey(member_replaced.getId())) {
            currentCommands.get(member_replaced.getId()).nextStep(event);
            return true;
        }
        return false;
    }

    // receive an interaction and dispatch it to LongCommand
    public static boolean receiveInteract(ButtonInteractionEvent event) {
        if (event.getInteraction().getMember().isEmpty()) return false;

        Member member_replaced = getMemberIdentity(event.getInteraction().getMember().get());
        if (currentCommands.containsKey(member_replaced.getId())) {
            currentCommands.get(member_replaced.getId()).nextStep(event);
            return true;
        }
        return false;
    }

    public static boolean hasCommand(Snowflake memberId) {
        return currentCommands.containsKey(memberId);
    }

    public static boolean hasCommand(LongCommand command) {
        return currentCommands.containsValue(command);
    }

    public static void removeCommand(Snowflake memberId) {
        currentCommands.remove(memberId);
    }

    public static void removeCommand(Snowflake memberId, Command command) {
        if (currentCommands.containsValue(command)) currentCommands.remove(memberId);
    }

    public static void left(Snowflake id) {
        if (currentCommands.containsKey(id)) {
            currentCommands.get(id).endCommand();
        }
    }

    public static int size() {
        return currentCommands.size();
    }

    /*
        DON'T USE THIS METHOD !!!
     */
    @Deprecated
    public static Map<Snowflake, LongCommand> getMap() {
        return currentCommands;
    }

    public static LongCommand getCommandOf(Snowflake id) {
        return currentCommands.get(id);
    }

    public static boolean containPerm(PermissionSet permissionToBeIn, PermissionSet PermissionsContainers) {
        AtomicBoolean atReturn = new AtomicBoolean(true);
        permissionToBeIn.stream().iterator().forEachRemaining(permission -> {
            AtomicBoolean haveFind = new AtomicBoolean(false);
            PermissionsContainers.stream().iterator().forEachRemaining(permission1 -> {
                if (permission.equals(permission1)) haveFind.set(true);
            });
            if (!haveFind.get()) atReturn.set(false);
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

    private static Member getMemberIdentity(Member member) {
        return logged_as.containsKey(member.getId()) ? MemberCache.get(logged_as.get(member.getId()).asString()) :
                member;
    }

    // ________________________________________________STATIC FUNCTION________________________________________________


    private static ArrayList<String> getClassNamesFromPackage(String packageName) throws IOException,
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

}