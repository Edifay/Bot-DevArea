package devarea.bot.automatical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.object_for_stock.Mission;
import devarea.bot.commands.with_out_text_starter.CreateMission;
import devarea.bot.data.ColorsUsed;
import devarea.bot.event.MemberJoin;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.http.client.ClientException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static devarea.bot.commands.Command.delete;

public class MissionsManager {

    public static Message messsage;
    private static ArrayList<Mission> missions = new ArrayList<>();

    public static void init() {
        load();
        sendLastMessage();
        new Thread(() -> {
            if (verif())
                save();
        });
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

    public static void react(ReactionAddEvent event) {
        if (event.getMessageId().equals(messsage.getId()))
            event.getMessage().block().removeReaction(event.getEmoji(), event.getUserId()).subscribe();

        if (event.getMessageId().equals(messsage.getId()) && event.getEmoji().equals(ReactionEmoji.custom(Init.idYes)) && !CommandManager.actualCommands.containsKey(event.getMember().get().getId())) {
            if (!MemberJoin.bindJoin.containsKey(event.getMember().get().getId()))
                synchronized (CommandManager.key) {
                    CommandManager.actualCommands.put(event.getMember().get().getId(), new CreateMission(event));
                }
            else
                Command.sendError((TextChannel) event.getChannel().block(), "Vous devez finir le questionnaire d'arrivé pour créer une commande !");
        }
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
        for (Mission mission : missions) {
            boolean isMemberHere = false;
            Message message = null;
            try {
                Init.devarea.getMemberById(Snowflake.of(mission.getMemberId())).block();
                isMemberHere = true;
            } catch (ClientException e) {
            }
            try {
                message = mission.getMessage().getMessage();
            } catch (ClientException e) {
            }
            if (!isMemberHere || message == null) {
                atRemove.add(mission);
                if (message != null)
                    delete(false, message);
            }
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

}
