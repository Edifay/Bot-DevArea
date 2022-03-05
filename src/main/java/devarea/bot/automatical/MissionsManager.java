package devarea.bot.automatical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.ConsumableCommand;
import devarea.bot.commands.object_for_stock.Mission;
import devarea.bot.commands.with_out_text_starter.CreateMission;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static devarea.Main.developing;
import static devarea.bot.commands.Command.delete;
import static devarea.bot.event.FunctionEvent.startAway;

public class MissionsManager {

    public static Message messsage;
    private static ArrayList<Mission> missions = new ArrayList<>();
    private static TextChannel channel;

    public static void init() {
        load();
        channel = (TextChannel) Init.devarea.getChannelById(Init.idMissionsPayantes).block();
        //if (!developing)
        Message msg = channel.getLastMessage().block();
        if (msg.getEmbeds().size() == 0 || msg.getEmbeds().get(0).getTitle().equals("Créer une mission."))
            sendLastMessage();
        else
            messsage = msg;
        new Thread(() -> {
            try {
                while (true) {
                    if (verif())
                        save();
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
                .addEmbed(EmbedCreateSpec.builder()
                        .color(ColorsUsed.same)
                        .title("Créer une mission.")
                        .description("Cliquez sur le bouton ci-dessous pour créer une mission !").build())
                .addComponent(ActionRow.of(Button.primary("createMission", "Créer une Mission")))
                .build(), true);
    }

    public static boolean interact(ButtonInteractionEvent event) {
        if (event.getCustomId().equals("createMission")) {
            CommandManager.addManualCommand(event.getInteraction().getMember().get(), new ConsumableCommand(CreateMission.class) {
                @Override
                protected Command command() {
                    return new CreateMission(this.member);
                }
            });
            return true;
        }
        return false;
    }

    public static void stop() {
        delete(true, messsage);
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
        if (!file.exists())
            save();
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
                channel.createMessage(MessageCreateSpec.builder()
                        .content("La mission de : <@" + mission.getMemberId() + "> a été supprimé !").build()).subscribe();
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

        if (atRemove.size() == 0)
            return false;

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
            if (missions.get(i).getMemberId().equals(id.asString()))
                newArray.add(missions.get(i));
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

}
