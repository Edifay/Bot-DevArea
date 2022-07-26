package devarea.bot.commands;

import devarea.Main;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.PermissionSet;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
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
import static devarea.global.utils.ThreadHandler.startAway;

public class CommandManager {

    private static final Map<String, Constructor> classBound = new HashMap<>();

    private static final Map<Snowflake, LongCommand> actualCommands = new HashMap<>();

    private static final Map<Snowflake, Snowflake> logged_as = new HashMap<>();

    public static void init() {
        Init.client.on(new ReactiveEventAdapter() {
            @Override
            public Publisher<?> onChatInputInteraction(ChatInputInteractionEvent event) {
                System.out.println(event.getCommandName());
                if (actualCommands.containsKey(event.getInteraction().getMember().get().getId())) {
                    event.reply(InteractionApplicationCommandCallbackSpec.builder()
                            .ephemeral(true)
                            .addEmbed(EmbedCreateSpec.builder()
                                    .title("Erreur !")
                                    .description("Vous êtes déjà actuellement dans une commande !")
                                    .color(ColorsUsed.wrong)
                                    .build())

                            .build()).subscribe();
                    return Mono.empty();
                }
                startAway(() -> exe(event.getCommandName(), null, event));
                return super.onChatInputInteraction(event);
            }
        }).subscribe();

        try {

            System.out.println("Loading commands :");

            ArrayList<String> names = getClassNamesFromPackage("devarea.bot.commands.inLine");
            if (names.size() == 0) {
                names = getClassNamesFromPackage("BOOT-INF.classes.devarea.bot.commands.inLine");
            }
            System.out.println("Class name found : " + Arrays.toString(names.toArray()));

            List<ApplicationCommandRequest> slashCommands = new ArrayList<>();

            for (String className : names) {
                if (!className.contains("$")) {
                    final String newName = className.startsWith("/") ? className.substring(1) : className;
                    System.out.print(className + "->" + newName + " | ");
                    Class<?> currentClass = Class.forName("devarea.bot.commands.inLine." + newName);
                    if (Arrays.stream(currentClass.getConstructors())
                            .anyMatch(constructor -> constructor.getGenericParameterTypes().length == 3
                                    && constructor.getGenericParameterTypes()[0].equals(Member.class)
                                    && constructor.getGenericParameterTypes()[1].equals(TextChannel.class)
                                    && constructor.getGenericParameterTypes()[2].equals(Message.class))) {

                        classBound.put(newName.toLowerCase(Locale.ROOT), currentClass.getConstructor(Member.class,
                                TextChannel.class, Message.class));
                    } else if (Arrays.stream(currentClass.getConstructors())
                            .anyMatch(constructor -> constructor.getGenericParameterTypes().length == 2
                                    && constructor.getGenericParameterTypes()[0].equals(Member.class)
                                    && constructor.getGenericParameterTypes()[1].equals(ChatInputInteractionEvent.class))) {
                        classBound.put(newName.toLowerCase(Locale.ROOT), currentClass.getConstructor(Member.class,
                                ChatInputInteractionEvent.class));
                        // If is slash command add it to the collection !
                        if (Arrays.asList(currentClass.getInterfaces()).contains(SlashCommand.class))
                            slashCommands.add(((SlashCommand) currentClass.getConstructor().newInstance()).getSlashCommandDefinition());
                    } else
                        System.err.println("\nImpossibilité de charger la commande : " + "devarea.bot.commands.inLine" +
                                "." + newName);

                }
            }
            System.out.println();

            Init.client.getRestClient().getApplicationService()
                    .bulkOverwriteGlobalApplicationCommand(Init.client.getRestClient().getApplicationId().block(),
                            slashCommands)
                    .subscribe();

            System.out.println(slashCommands.size() + " slash commands on " + classBound.size() + " commands loaded " +
                    "!\n" + Main.separator);
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.err.println("The SlashCommand need an empty constructor to work !\n\n" + e.getMessage());
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
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

    public static void exe(String command, @Nullable final MessageCreateEvent message,
                           @Nullable final ChatInputInteractionEvent chatInteraction) {
        command = command.toLowerCase(Locale.ROOT);
        if (classBound.containsKey(command)) {
            try {
                Member member_command = message != null ? message.getMember().get() :
                        chatInteraction.getInteraction().getMember().get();
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
                    Command actualCommand;
                    if (message != null) {
                        if (classBound.get(command).getParameterCount() == 2) {
                            sendError((TextChannel) ChannelCache.get(message.getMessage().getChannelId().asString()),
                                    "Cette commande à migré vers les ``slash`` commandes !");
                            return;
                        } else
                            actualCommand = (Command) classBound.get(command).newInstance(member_replaced,
                                    ChannelCache.get(message.getMessage().getChannelId().asString()),
                                    message.getMessage());

                    } else {
                        actualCommand = (Command) classBound.get(command).newInstance(member_replaced,
                                chatInteraction);
                    }
                    if (actualCommand instanceof LongCommand)
                        actualCommands.put(member_replaced.getId(), (LongCommand) actualCommand);
                } else if (message != null)
                    Command.sendError((TextChannel) ChannelCache.watch(message.getMessage().getChannelId().asString()), "Vous n'avez pas " +
                            "la permission d'exécuter cette commande !");
                else
                    chatInteraction.reply(InteractionApplicationCommandCallbackSpec.builder()
                            .ephemeral(true)
                            .addEmbed(EmbedCreateSpec.builder()
                                    .color(ColorsUsed.wrong)
                                    .title("Erreur !")
                                    .description("Vous n'avez pas la permission d'éxécuter cette commande !")
                                    .build())
                            .build()).subscribe();

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

    public static boolean addManualCommand(Member member, ConsumableCommand command) {
        return addManualCommand(member, command, false);
    }

    public static boolean addManualCommand(Member member, ConsumableCommand command, final boolean force_join) {
        try {
            Member member_command = member;
            Member member_replaced = logged_as.containsKey(member_command.getId()) ?
                    MemberCache.get(logged_as.get(member_command.getId()).asString()) : member_command;
            PermissionSet permissionSet = null;
            try {
                PermissionCommand defaultCommand =
                        (PermissionCommand) command.commandClass.getConstructor(PermissionCommand.class).newInstance((PermissionCommand) () -> null);
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
            Command.sendError(command.channel, "Vous n'avez pas la permission d'exécuter cette commande !");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean react(ReactionAddEvent event) {
        Member member_replaced = logged_as.get(event.getUserId()) == null ? event.getMember().get() :
                MemberCache.get(logged_as.get(event.getUserId()).asString());
        if (actualCommands.containsKey(member_replaced.getId())) {
            actualCommands.get(member_replaced.getId()).nextStep(event);
            return true;
        }
        return false;
    }

    public static boolean receiveMessage(MessageCreateEvent event) {
        Member member_replaced = logged_as.get(event.getMessage().getAuthor().get().getId()) == null ?
                event.getMember().get() :
                MemberCache.get(logged_as.get(event.getMessage().getAuthor().get().getId()).asString());
        if (actualCommands.containsKey(member_replaced.getId())) {
            actualCommands.get(member_replaced.getId()).nextStep(event);
            return true;
        }
        return false;
    }

    public static boolean receiveInteract(ButtonInteractionEvent event) {
        if (event.getInteraction().getMember().isEmpty()) return false;
        Member member_replaced = logged_as.get(event.getInteraction().getMember().get().getId()) == null ?
                event.getInteraction().getMember().get() :
                MemberCache.get(logged_as.get(event.getInteraction().getMember().get().getId()).asString());
        if (actualCommands.containsKey(member_replaced.getId())) {
            actualCommands.get(member_replaced.getId()).nextStep(event);
            return true;
        }
        return false;
    }

    public static boolean hasCommand(Snowflake memberId) {
        return actualCommands.containsKey(memberId);
    }

    public static boolean hasCommand(LongCommand command) {
        return actualCommands.containsValue(command);
    }

    public static void removeCommand(Snowflake memberId) {
        actualCommands.remove(memberId);
    }

    public static void removeCommand(Snowflake memberId, Command command) {
        if (actualCommands.containsValue(command))
            actualCommands.remove(memberId);
    }

    public static void left(Snowflake id) {
        if (actualCommands.containsKey(id)) {
            actualCommands.get(id).endCommand();
        }
    }

    public static int size() {
        return actualCommands.size();
    }

    /*
        DON'T USE THIS METHOD !!!
     */
    @Deprecated
    public static Map<Snowflake, LongCommand> getMap() {
        return actualCommands;
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