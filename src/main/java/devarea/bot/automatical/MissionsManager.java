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
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static devarea.bot.commands.Command.delete;
import static devarea.bot.event.FunctionEvent.startAway;

public class MissionsManager {

    public static Message messsage;
    private static ArrayList<Mission> missions = new ArrayList<>();

    public static void init() {
        load();
        sendLastMessage();
        new Thread(() -> {
            try {
                while (true) {
                    if (verif())
                        save();
                    Thread.sleep(86400000L);
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
        messsage = Command.sendEmbed((TextChannel) Init.devarea.getChannelById(Init.idMissionsPayantes).block(), embedCreateSpec -> {
            embedCreateSpec.setColor(ColorsUsed.same);
            embedCreateSpec.setTitle("Créer une mission.");
            embedCreateSpec.setDescription("Cliquez sur <:ayy:" + Init.idYes.getId().asString() + "> pour créer une mission !");
        }, true);
        messsage.addReaction(ReactionEmoji.custom(Init.idYes)).subscribe();
    }

    public static boolean react(ReactionAddEvent event) {
        if (event.getMessageId().equals(messsage.getId())) {
            startAway(() -> event.getMessage().block().removeReaction(event.getEmoji(), event.getUserId()).subscribe());

            if (event.getEmoji().equals(ReactionEmoji.custom(Init.idYes)))
                CommandManager.addManualCommand(event.getMember().get(), new ConsumableCommand((TextChannel) event.getChannel().block(), CreateMission.class) {
                    @Override
                    protected Command command() {
                        return new CreateMission(event);
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
        for (Mission mission : missions)
            if (!Init.membersId.contains(Snowflake.of(mission.getMemberId()))) {
                /*((TextChannel) Init.devarea.getChannelById(Init.idMissionsPayantes).block()).createMessage(messageCreateSpec -> {
                    messageCreateSpec.setContent("Le membre : <@" + mission.getMemberId() + "> est concidéré comme \"left\" ça missions devrait être supprimer !");
                }).block();*/
                /*atRemove.add(mission);
                try {
                    delete(false, mission.getMessage().getMessage());
                } catch (Exception e) {
                }*/
            }

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
