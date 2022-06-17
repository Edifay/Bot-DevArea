package devarea.bot.automatical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.Main;
import devarea.backend.controllers.rest.requestContent.RequestHandlerGlobal;
import devarea.bot.cache.ChannelCache;
import devarea.bot.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.automatical.handlerData.MissionManagerData;
import devarea.bot.cache.tools.childs.CachedChannel;
import devarea.bot.commands.Command;
import devarea.bot.commands.commandTools.MessageSeria;
import devarea.bot.commands.commandTools.Mission;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.*;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static devarea.bot.commands.Command.delete;
import static devarea.bot.commands.Command.send;
import static devarea.bot.event.FunctionEvent.startAway;

public class MissionsHandler {

    public static Message message;
    private static LinkedHashMap<String, Mission> missions = new LinkedHashMap<>();
    private static ArrayList<MissionManagerData.MissionFollow> missionsFollow = new ArrayList<>();
    private static TextChannel channel;
    private static int missionFollowId = 0;

    private static final RequestHandlerGlobal.PasswordGenerator generator =
            new RequestHandlerGlobal.PasswordGenerator(new RequestHandlerGlobal.PasswordGenerator.PasswordGeneratorBuilder()
                    .useDigits(true)
                    .useLower(true));

    public static void init() {
        load();
        channel = (TextChannel) ChannelCache.fetch(Init.initial.paidMissions_channel.asString());
        Message msg = channel.getLastMessage().block();
        if (msg.getEmbeds().size() == 0 || msg.getEmbeds().get(0).getTitle().equals("Créer une mission."))
            sendLastMessage();
        else message = msg;
        new Thread(() -> {
            try {
                while (true) {
                    if (verif()) save();
                    if (!Main.developing)
                        checkForUpdate();
                    Thread.sleep(3600000);
                }
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    public static void update() {
        delete(false, message);
        sendLastMessage();
    }

    private static void sendLastMessage() {
        message = Command.send(channel, MessageCreateSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder().color(ColorsUsed.same)
                                .title("Créer une mission.")
                                .description("Cliquez sur le bouton ci-dessous pour créer une mission !" +
                                        "!\n\nVisionner les missions sur web -> " + Main.domainName + "missions")
                                .build())
                        .addComponent(ActionRow.of(Button.link(Main.domainName + "mission-creator", "devarea.fr"))).build(),
                true);
    }

    public static boolean interact(final ButtonInteractionEvent event) {
        if (event.getCustomId().startsWith("mission"))
            return actionToUpdateMessage(event);
        else if (event.getCustomId().equals("took_mission"))
            return actionToTookMission(event);
        else if (event.getCustomId().equals("followMission_close"))
            return actionToCloseFollowedMission(event);

        return false;
    }

    public static void add(Mission mission) {
        LinkedHashMap<String, Mission> temp = (LinkedHashMap<String, Mission>) missions.clone();
        missions.clear();
        missions.put(mission.getId(), mission);
        missions.putAll(temp);

        save();
    }

    public static void remove(Mission mission) {
        missions.remove(mission.getId(), mission);
        save();
    }

    public static void clearThisMission(Mission mission) {
        missions.remove(mission.getId(), mission);
        startAway(() -> {
            try {
                delete(true, mission.getMessage().getMessage());
            } catch (Exception ignored) {
            }
        });
        save();
    }

    private static void load() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("./mission.json");
        if (!file.exists()) save();
        try {

            MissionManagerData missionData = mapper.readValue(file, new TypeReference<>() {
            });

            missionData.missions.forEach((mission) -> {
                if (mission.getId().equals("0"))
                    mission.setId(generateID());
                missions.put(mission.getId(), mission);
            });

            missionFollowId = missionData.missionFollowId;
            missionsFollow = missionData.missionsFollow;

            System.out.println("Missions loaded : " + missions.size() + " detected !");

            save();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T, Q> LinkedHashMap<T, Q> reverseMap(LinkedHashMap<T, Q> toReverse) {
        LinkedHashMap<T, Q> reversedMap = new LinkedHashMap<>();
        List<T> reverseOrderedKeys = new ArrayList<>(toReverse.keySet());
        Collections.reverse(reverseOrderedKeys);
        reverseOrderedKeys.forEach((key) -> reversedMap.put(key, toReverse.get(key)));
        return reversedMap;
    }

    public static boolean verif() {
        ArrayList<Mission> atRemove = new ArrayList<>();
        for (Mission mission : missions.values())
            if (!MemberCache.contain(mission.getMemberId())) {
                atRemove.add(mission);
                startAway(() -> {
                    try {
                        delete(false, mission.getMessage().getMessage());
                    } catch (Exception ignored) {
                    }
                });
            }

        if (atRemove.size() == 0) return false;

        System.out.println("Il y a en tout : " + atRemove.size() + " missions à supprimer !");

        for (Mission mission : atRemove)
            missions.remove(mission.getId(), mission);

        return true;
    }

    public static void save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            MissionManagerData missionData = new MissionManagerData(missions, missionFollowId, missionsFollow);
            mapper.writeValue(new File("./mission.json"), missionData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Mission> getOf(Snowflake id) {
        ArrayList<Mission> newArray = new ArrayList<>();
        for (Mission mission : missions.values())
            if (mission.getMemberId().equals(id.asString()))
                newArray.add(mission);

        return newArray;
    }

    /*
    Do not use this method
     */
    public static ArrayList<Mission> getMissions() {
        return new ArrayList<>(missions.values());
    }

    public static void checkForUpdate() {
        ArrayList<Mission> spoiled_missions = new ArrayList<>();

        for (Mission mission : missions.values())
            if (mission.getMessage_verification() == null && System.currentTimeMillis() - mission.getLast_update() > 604800000)
                askValidate(mission);
            else if (mission.getMessage_verification() != null && System.currentTimeMillis() - mission.getLast_update() > 864000000)
                spoiled_missions.add(mission);

        for (Mission mission : spoiled_missions)
            validateSpoilAction(mission);

    }

    public static boolean actionToUpdateMessage(final ButtonInteractionEvent event) {
        Mission current_mission = null;
        for (Mission mission : missions.values())
            if (mission.getMessage_verification() != null && mission.getMessage_verification().getMessageID().equals(event.getMessageId()))
                current_mission = mission;

        if (current_mission != null) {
            if (event.getCustomId().equals("mission_yes")) {
                ((TextChannel) ChannelCache.watch(event.getInteraction().getChannelId().asString())).getMessageById(current_mission.getMessage_verification().getMessageID()).block().edit(MessageEditSpec.builder().addEmbed(EmbedCreateSpec.builder()
                                .title("Mission actualisé !")
                                .description("La mission : **" + current_mission.getTitle() + "**, a été défini comme" +
                                        " valide pour encore 7 jours.\n\nVous recevrez une nouvelle demande de " +
                                        "validation dans 7 jours.")
                                .color(ColorsUsed.just).build())
                        .components(new ArrayList<>())
                        .build()).block();
                current_mission.update();
                current_mission.setMessage_verification(null);
                save();
            } else if (event.getCustomId().equals("mission_no")) {
                ((TextChannel) ChannelCache.watch(event.getInteraction().getChannelId().asString())).getMessageById(current_mission.getMessage_verification().getMessageID()).block().edit(MessageEditSpec.builder().addEmbed(EmbedCreateSpec.builder()
                                .title("Mission supprimé !")
                                .description("La mission : **" + current_mission.getTitle() + "**, a été " +
                                        "définitivement supprimé !")
                                .color(ColorsUsed.just).build())
                        .components(new ArrayList<>())
                        .build()).block();
                clearThisMission(current_mission);
            }
            return true;
        }
        return false;
    }

    public static void askValidate(Mission mission) {
        Member mission_member = MemberCache.get(mission.getMemberId());
        Message message = mission_member.getPrivateChannel().block().createMessage(MessageCreateSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Vérification de la validité d'une mission.")
                        .description("Vous avez une mission actuellement active !\n\nLe titre de cette mission est : " +
                                "**" + mission.getTitle() + "**\n\nIl vous reste 3 jours pour nous comfirmer ou non " +
                                "si cette mission est-elle toujours d'actualité ?\n\nSi oui : <:ayy:" + Init.idYes.getId().asString() + "> si non : <:ayy:" + Init.idNo.getId().asString() + ">.")
                        .color(ColorsUsed.same).build())
                .addComponent(ActionRow.of(Button.primary("mission_yes", ReactionEmoji.custom(Init.idYes)),
                        Button.primary("mission_no", ReactionEmoji.custom(Init.idNo))))
                .build()).block();
        mission.setLast_update(System.currentTimeMillis() - 604800000);
        mission.setMessage_verification(new MessageSeria(message));
        save();
    }

    public static void validateSpoilAction(Mission mission) {
        Member mission_member = MemberCache.get(mission.getMemberId());
        mission_member.getPrivateChannel().block().getMessageById(mission.getMessage_verification().getMessageID()).block()
                .edit(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Mission supprimé !")
                                .description("Le délais des 3 jours a été expiré. La mission : **" + mission.getTitle() + "**, a été définitivement supprimé !")
                                .color(ColorsUsed.wrong).build())
                        .components(new ArrayList<>()).build())
                .subscribe();
        clearThisMission(mission);
    }

    public static boolean actionToTookMission(final ButtonInteractionEvent event) {
        Mission mission = getMissionOfMessage(event.getMessageId());
        Snowflake member_react_id = event.getInteraction().getMember().get().getId();
        if (mission != null) {
            if (mission.getMemberId().equals(member_react_id.asString())) {
                event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .ephemeral(true)
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Error !")
                                .description("Vous ne pouvez pas prendre votre propre mission !")
                                .color(ColorsUsed.wrong)
                                .build())
                        .build()).subscribe();
                return true;
            }
            if (alreadyHaveAChannel(mission.getMemberId(), member_react_id.asString())) {
                event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .ephemeral(true)
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Error !")
                                .description("Vous suivez déjà cette mission !")
                                .color(ColorsUsed.wrong)
                                .build())
                        .build()).subscribe();
                return true;
            }
            followThisMission(mission, member_react_id);
            return true;
        }
        return false;
    }

    public static String tookMissionFromWeb(final String missionId, final Member member) {
        Mission mission = get(missionId);
        Snowflake member_react_id = member.getId();
        if (mission != null) {
            if (mission.getMemberId().equals(member_react_id.asString()))
                return "Vous ne pouvez pas prendre votre propre mission !";
            if (alreadyHaveAChannel(mission.getMemberId(), member_react_id.asString()))
                return "Vous avez déjà pris cette commande !";

            followThisMission(mission, member_react_id);
            return "Vous avez pris cette mission !";
        }
        return "La mission n'a pas été trouvé !";
    }

    private static void followThisMission(Mission mission, Snowflake member_react_id) {
        Set<PermissionOverwrite> set = new HashSet<>();
        set.add(PermissionOverwrite.forRole(Init.initial.rulesAccepted_role, PermissionSet.of(),
                PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forRole(Init.devarea.getEveryoneRole().block().getId(), PermissionSet.of(),
                PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forMember(member_react_id, PermissionSet.of(Permission.VIEW_CHANNEL,
                Permission.READ_MESSAGE_HISTORY, Permission.SEND_MESSAGES), PermissionSet.of()));
        set.add(PermissionOverwrite.forMember(Snowflake.of(mission.getMemberId()),
                PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY,
                        Permission.SEND_MESSAGES), PermissionSet.of()));
        set.add(PermissionOverwrite.forRole(Snowflake.of("777782222920744990"),
                PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of()));
        set.add(PermissionOverwrite.forRole(Snowflake.of("768383784571240509"),
                PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of()));

        TextChannel channel = Init.devarea.createTextChannel(TextChannelCreateSpec.builder()
                .parentId(Snowflake.of("964757205184299028"))
                .name("Suivis de mission n°" + ++missionFollowId)
                .permissionOverwrites(set)
                .build()).block();
        int id = missionFollowId;

        send(channel, MessageCreateSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title(mission.getTitle())
                        .description(mission.getDescriptionText() + "\n\nPrix: " + mission.getBudget() + "\nDate " +
                                "de" +
                                " retour: " + mission.getDeadLine() + "\nType de support: " + mission.getSupport()
                                + "\nLangage: " + mission.getLanguage() + "\nNiveau estimé: " + mission.getNiveau())
                        .color(ColorsUsed.just)
                        .build())
                .build(), false);

        Message message = channel.createMessage(MessageCreateSpec.builder()
                .content("<@" + member_react_id.asString() + "> -> <@" + mission.getMemberId() + ">")
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Suivis de Mission !")
                        .description("Bienvenue dans ce channel !\n\n" +
                                "Ce channel a été créé car <@" + member_react_id.asString() + "> est intéressé " +
                                "par la mission de <@" + mission.getMemberId() + ">." +
                                "\n\nCe channel est dédié pour vous, ainsi qu'à la mise en place de la mission et" +
                                " nous vous demandons de passer exclusivement par ce channel pour toute " +
                                "discussion à propos de celle-ci." +
                                "\n\nCeci a pour but d'augmenter la fiabilité des clients et des développeurs " +
                                "pour qu'une mission puisse se passer de la meilleure des manières" +
                                ".\nRèglementation des missions : <#768435208906735656>." +
                                "\n\nVous pouvez clôturer ce channel à tout moment !")
                        .color(ColorsUsed.same)
                        .build())
                .addComponent(ActionRow.of(Button.secondary("followMission_close", "Cloturer le channel")))
                .build()).block();

        missionsFollow.add(new MissionManagerData.MissionFollow(id, new MessageSeria(message),
                mission.getMemberId(), member_react_id.asString()));

        save();
    }

    public static Mission getMissionOfMessage(final Snowflake message_id) {
        for (Mission mission : missions.values())
            if (mission.getMessage().getMessageID().equals(message_id))
                return mission;
        return null;
    }

    public static boolean alreadyHaveAChannel(final String clientID, final String devID) {
        for (MissionManagerData.MissionFollow mission : missionsFollow)
            if (mission.clientID.equals(clientID) && mission.devID.equals(devID))
                return true;
        return false;
    }

    public static boolean actionToCloseFollowedMission(final ButtonInteractionEvent event) {
        final MissionManagerData.MissionFollow mission = getByMessageID(event.getMessageId());
        if (mission != null) {

            send(((TextChannel) ChannelCache.watch(event.getInteraction().getChannelId().asString())),
                    MessageCreateSpec.builder()
                            .addEmbed(EmbedCreateSpec.builder()
                                    .title("Clôture du Suivis de mission.")
                                    .description("La clôture du suivis a été éxécuté par : <@" + event.getInteraction().getMember().get().getId().asString() + ">. Le suivis fermera dans 1 heure.")
                                    .color(ColorsUsed.same)
                                    .timestamp(Instant.now())
                                    .build())
                            .build(), false);

            mission.messageSeria.getMessage().edit(MessageEditSpec.builder()
                    .components(new ArrayList<>())
                    .build()).subscribe();

            startAway(() -> {

                try {
                    Thread.sleep(3600000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    missionsFollow.remove(mission);

                    Set<PermissionOverwrite> set = new HashSet<>();
                    set.add(PermissionOverwrite.forRole(Init.initial.rulesAccepted_role, PermissionSet.of(),
                            PermissionSet.of(Permission.VIEW_CHANNEL)));
                    set.add(PermissionOverwrite.forRole(Init.devarea.getEveryoneRole().block().getId(),
                            PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL)));
                    set.add(PermissionOverwrite.forMember(Snowflake.of(mission.clientID), PermissionSet.of(),
                            PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY,
                                    Permission.SEND_MESSAGES)));
                    set.add(PermissionOverwrite.forMember(Snowflake.of(mission.devID), PermissionSet.of(),
                            PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY,
                                    Permission.SEND_MESSAGES)));
                    set.add(PermissionOverwrite.forRole(Snowflake.of("777782222920744990"),
                            PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of()));
                    set.add(PermissionOverwrite.forRole(Snowflake.of("768383784571240509"),
                            PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of()));

                    ((TextChannel) ChannelCache.watch(event.getInteraction().getChannelId().asString())).edit(TextChannelEditSpec.builder()
                            .name("Closed n°" + mission.missionID)
                            .permissionOverwrites(set)
                            .build()).subscribe();
                    EmbedCreateSpec embed = EmbedCreateSpec.builder()
                            .title("Clôture du suivis n°" + mission.missionID + " !")
                            .description("Le suivis de mission n°" + mission.missionID + " a été clôturé à la demande" +
                                    " de <@" + event.getInteraction().getMember().get().getId().asString() + ">.")
                            .color(ColorsUsed.just)
                            .build();
                    startAway(() -> MemberCache.get(mission.clientID).getPrivateChannel().block().createMessage(embed).subscribe());
                    startAway(() -> MemberCache.get(mission.devID).getPrivateChannel().block().createMessage(embed).subscribe());

                    save();
                }
            });
        }
        return false;
    }

    public static MissionManagerData.MissionFollow getByMessageID(final Snowflake messageID) {
        for (MissionManagerData.MissionFollow mission : missionsFollow)
            if (mission.messageSeria.getMessageID().equals(messageID))
                return mission;
        return null;
    }

    public static Mission createMission(final String title, final String description, final String prix,
                                        final String dateRetour, final String langage, final String support,
                                        final String niveau, final Member member) {
        Mission createdMission = new Mission(title, description, prix, dateRetour, langage, support, niveau,
                member.getId().asString(), null);
        createdMission.setMessage(new MessageSeria(
                Objects.requireNonNull(send((TextChannel) ChannelCache.watch(Init.initial.paidMissions_channel.asString()), MessageCreateSpec.builder()
                        .content("**Mission proposée par <@" + member.getId().asString() + "> :**")
                        .addEmbed(EmbedCreateSpec.builder()
                                .title(title)
                                .description(description + "\n\nPrix: " + prix + "\nDate de retour: " + dateRetour +
                                        "\nType de support: " + support + "\nLangage: " + langage + "\nNiveau estimé:" +
                                        " " + niveau + "\n\nCette mission est posté par : " + "<@" + member.getId().asString() + ">.")
                                .color(ColorsUsed.just)
                                .author(member.getDisplayName(), member.getAvatarUrl(), member.getAvatarUrl())
                                .timestamp(Instant.now())
                                .build())
                        .addComponent(ActionRow.of(Button.link(Main.domainName + "mission?id=" + createdMission.getId(),
                                "devarea.fr"), Button.secondary("took_mission", "Prendre la mission")))
                        .build(), true))
        ));
        MissionsHandler.add(createdMission);
        MissionsHandler.update();
        return createdMission;
    }

    public static Mission get(final String id) {
        return missions.get(id);
    }

    public static String generateID() {
        return generator.generate(20);
    }
}
