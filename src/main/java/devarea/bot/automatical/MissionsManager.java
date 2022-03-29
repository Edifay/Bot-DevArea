package devarea.bot.automatical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.ConsumableCommand;
import devarea.bot.commands.object_for_stock.MessageSeria;
import devarea.bot.commands.object_for_stock.Mission;
import devarea.bot.commands.with_out_text_starter.CreateMission;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static devarea.bot.commands.Command.delete;
import static devarea.bot.event.FunctionEvent.startAway;

public class MissionsManager {

    public static Message messsage;
    private static ArrayList<Mission> missions = new ArrayList<>();
    private static TextChannel channel;

    public static void init() {
        load();
        channel = (TextChannel) Init.devarea.getChannelById(Init.idMissionsPayantes).block();
        Message msg = channel.getLastMessage().block();
        if (msg.getEmbeds().size() == 0 || msg.getEmbeds().get(0).getTitle().equals("Créer une mission."))
            sendLastMessage();
        else messsage = msg;
        new Thread(() -> {
            try {
                while (true) {
                    if (verif()) save();
                    checkForUpdate();
                    Thread.sleep(3600000);
                }
            } catch (InterruptedException e) {
            }
        }).start();
    }

    public static void update() {
        delete(false, messsage);
        sendLastMessage();
    }

    private static void sendLastMessage() {
        messsage = Command.send(channel, MessageCreateSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder().color(ColorsUsed.same)
                                .title("Créer une mission.")
                                .description("Cliquez sur le bouton ci-dessous pour créer une mission !").build())
                        .addComponent(ActionRow.of(Button.primary("createMission", "Créer une Mission"))).build(),
                true);
    }

    public static boolean interact(final ButtonInteractionEvent event) {
        if (event.getCustomId().equals("createMission")) {
            CommandManager.addManualCommand(event.getInteraction().getMember().get(), new ConsumableCommand(CreateMission.class) {
                @Override
                protected Command command() {
                    return new CreateMission(this.member);
                }
            });
            return true;
        } else if (event.getCustomId().startsWith("mission")) if (actionToUpdateMessage(event)) return true;

        return false;
    }

    public static void add(Mission mission) {
        missions.add(mission);
        save();
    }

    public static void remove(Mission mission) {
        missions.remove(mission);
        save();
    }

    public static void clearThisMission(Mission mission) {
        missions.remove(mission);
        startAway(() -> {
            try {
                delete(true, mission.getMessage().getMessage());
            } catch (Exception e) {

            }
        });
        save();
    }

    private static void load() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("./mission.json");
        if (!file.exists()) save();
        try {
            missions = mapper.readValue(file, new TypeReference<ArrayList<Mission>>() {
            });
            System.out.println("Missions loaded : " + missions.size() + " detected !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean verif() {
        ArrayList<Mission> atRemove = new ArrayList<>();
        TextChannel channel = (TextChannel) Init.devarea.getChannelById(Init.idMeetupVerif).block();
        for (Mission mission : missions) {
            if (!Init.membersId.contains(Snowflake.of(mission.getMemberId()))) {
                atRemove.add(mission);
                startAway(() -> {
                    try {
                        delete(false, mission.getMessage().getMessage());
                    } catch (Exception e) {
                    }
                });
            }
        }

        System.out.println("Il y a en tout : " + atRemove.size() + " missions à supprimer !");

        if (atRemove.size() == 0) return false;

        missions.removeAll(atRemove);
        return true;
    }

    public static void save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("./mission.json"), missions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Mission> getOf(Snowflake id) {
        ArrayList<Mission> newArray = new ArrayList<>();
        for (int i = 0; i < missions.size(); i++) {
            if (missions.get(i).getMemberId().equals(id.asString())) newArray.add(missions.get(i));
        }
        return newArray;
    }

    /*
    Do not use this method
     */
    @Deprecated
    public static ArrayList<Mission> getMissions() {
        return missions;
    }

    public static void checkForUpdate() {
        ArrayList<Mission> spoiled_missions = new ArrayList<>();

        for (Mission mission : missions)
            if (mission.getMessage_verification() == null && System.currentTimeMillis() - mission.getLast_update() > 604800000)
                askValidate(mission);
            else if (mission.getMessage_verification() != null && System.currentTimeMillis() - mission.getLast_update() > 864000000)
                spoiled_missions.add(mission);

        for (Mission mission : spoiled_missions)
            validateSpoilAction(mission);

    }

    public static boolean actionToUpdateMessage(final ButtonInteractionEvent event) {
        Mission current_mission = null;
        for (Mission mission : missions)
            if (mission.getMessage_verification() != null && mission.getMessage_verification().getMessageID().equals(event.getMessageId()))
                current_mission = mission;

        if (current_mission != null) {
            if (event.getCustomId().equals("mission_yes")) {
                event.getInteraction().getChannel().block().getMessageById(current_mission.getMessage_verification().getMessageID()).block().edit(MessageEditSpec.builder().addEmbed(EmbedCreateSpec.builder()
                                .title("Mission actualisé !")
                                .description("La mission : **" + current_mission.getTitle() + "**, a été défini comme valide pour encore 7 jours.\n\nVous recevrez une nouvelle demande de validation dans 7 jours.")
                                .color(ColorsUsed.just).build())
                        .components(new ArrayList<>())
                        .build()).block();
                current_mission.update();
                current_mission.setMessage_verification(null);
                System.out.println(event.getInteraction().getUser().getUsername() + " a prolongé la validité de sa mission de 7 jours.");
                save();
            } else if (event.getCustomId().equals("mission_no")) {
                event.getInteraction().getChannel().block().getMessageById(current_mission.getMessage_verification().getMessageID()).block().edit(MessageEditSpec.builder().addEmbed(EmbedCreateSpec.builder()
                                .title("Mission supprimé !")
                                .description("La mission : **" + current_mission.getTitle() + "**, a été définitivement supprimé !")
                                .color(ColorsUsed.just).build())
                        .components(new ArrayList<>())
                        .build()).block();
                clearThisMission(current_mission);
                System.out.println(event.getInteraction().getUser().getUsername()+" a supprimé sa mission !");
            }
            return true;
        }

        return false;
    }

    public static void askValidate(Mission mission) {
        Member mission_member = Init.devarea.getMemberById(Snowflake.of(mission.getMemberId())).block();
        System.out.println("Une vérification de validité de mission a été envoyé à : " + mission_member.getDisplayName());
        Message message = mission_member.getPrivateChannel().block().createMessage(MessageCreateSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Vérification de la validité d'une mission.")
                        .description("Vous avez une mission actuellement active !\n\nLe titre de cette mission est : **" + mission.getTitle() + "**\n\nIl vous reste 3 jours pour nous comfirmer ou non si cette mission est-elle toujours d'actualité ?\n\nSi oui : <:ayy:" + Init.idYes.getId().asString() + "> si non : <:ayy:" + Init.idNo.getId().asString() + ">.")
                        .color(ColorsUsed.same).build())
                .addComponent(ActionRow.of(Button.primary("mission_yes", ReactionEmoji.custom(Init.idYes)), Button.primary("mission_no", ReactionEmoji.custom(Init.idNo))))
                .build()).block();
        mission.setLast_update(System.currentTimeMillis() - 604800000);
        mission.setMessage_verification(new MessageSeria(message));
        save();
    }

    public static void validateSpoilAction(Mission mission) {
        Member mission_member = Init.devarea.getMemberById(Snowflake.of(mission.getMemberId())).block();
        System.out.println("Une vérification de validité de mission a expirée pour le membre : " + mission_member.getDisplayName());
        mission_member.getPrivateChannel().block().getMessageById(mission.getMessage_verification().getMessageID()).block()
                .edit(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Mission supprimé !")
                                .description("Le délais des 3 jours a été expiré. La mission : **" + mission.getTitle() + "**, a été définitivement supprimé !")
                                .color(ColorsUsed.wrong).build())
                        .components(new ArrayList<>()).build())
                .block();
        clearThisMission(mission);
    }

}
