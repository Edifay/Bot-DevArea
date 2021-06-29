package devarea.automatical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.Main;
import devarea.commands.Command;
import devarea.commands.CommandManager;
import devarea.commands.object_for_stock.FreeLance;
import devarea.commands.with_out_text_starter.CreateFreeLance;
import devarea.data.ColorsUsed;
import devarea.event.MemberJoin;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static devarea.commands.Command.delete;

public class FreeLanceManager {

    public static Message messsage;
    private static ArrayList<FreeLance> freeLances = new ArrayList<>();

    private final static ArrayList<FreeLance> bumpedFreeLance = new ArrayList<>();

    public static void init() {
        load();
        if (verif())
            save();
        sendLastMessage();
    }

    public static void update() {
        delete(false, messsage);
        sendLastMessage();
    }

    private static void sendLastMessage() {
        messsage = Command.sendEmbed((TextChannel) Main.devarea.getChannelById(Main.idFreeLance).block(), embedCreateSpec -> {
            embedCreateSpec.setColor(ColorsUsed.same);
            embedCreateSpec.setTitle("Proposez vos services !");
            embedCreateSpec.setDescription("Cliquez sur <:ayy:" + Main.idYes.getId().asString() + "> pour créer une page de compétances !");
            embedCreateSpec.setFooter("Cette fonctionnalité est en avant première, si vous voyez le moindre bug, veuillez nous alerter !", null);
        }, true);
        messsage.addReaction(ReactionEmoji.custom(Main.idYes)).subscribe();
    }

    public static void react(ReactionAddEvent event) {
        if (event.getMessageId().equals(messsage.getId()))
            event.getMessage().block().removeReaction(event.getEmoji(), event.getUserId()).subscribe();
        System.out.println("Reacted to freeLance !");
        if (event.getMessageId().equals(messsage.getId()) && event.getEmoji().equals(ReactionEmoji.custom(Main.idYes)) && !CommandManager.actualCommands.containsKey(event.getMember().get().getId())) {
            if (!MemberJoin.bindJoin.containsKey(event.getMember().get().getId()))
                if (!FreeLanceManager.hasFreelance(event.getMember().get()))
                    synchronized (CommandManager.key) {
                        CommandManager.actualCommands.put(event.getMember().get().getId(), new CreateFreeLance(event));
                    }
                else
                    Command.sendError((TextChannel) event.getChannel().block(), "Vous avez déjà une page FreeLance utilisez la commande `//freelance` pour effectué des modifications.");
            else
                Command.sendError((TextChannel) event.getChannel().block(), "Vous devez finir le questionnaire d'arrivé pour créer une commande !");
        }
    }

    public static boolean hasFreelance(Member member) {
        synchronized (freeLances) {
            for (FreeLance freeLance : freeLances)
                if (freeLance.getMemberId().equals(member.getId().asString())) return true;
        }
        return false;
    }

    public static FreeLance getFreeLance(Member member) {
        synchronized (freeLances) {
            for (FreeLance freeLance : freeLances)
                if (freeLance.getMemberId().equals(member.getId().asString())) return freeLance;
        }
        return null;
    }

    public static boolean bumpFreeLance(Member member) {
        synchronized (bumpedFreeLance) {
            if (!hasFreelance(member)) return false;
            FreeLance freeLance = getFreeLance(member);
            if (bumpedFreeLance.contains(freeLance))
                return false;
            else {
                delete(false, freeLance.getMessage().getMessage());
                freeLance.setMessage(new MessageSeria(Command.sendEmbed((TextChannel) Main.devarea.getChannelById(Main.idFreeLance).block(), freeLance.getEmbed(), true)));
                update();
                save();
                bumpedFreeLance.add(freeLance);
                new Thread(() -> {
                    try {
                        Thread.sleep(86400000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        synchronized (bumpedFreeLance) {
                            bumpedFreeLance.remove(freeLance);
                        }
                    }
                }).start();
                return true;
            }
        }
    }


    public static void stop() {
        delete(true, messsage);
    }

    public static void add(FreeLance mission) {
        freeLances.add(mission);
        save();
    }

    public static void remove(FreeLance mission) {
        freeLances.remove(mission);
        save();
    }

    private static void load() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("./freelance.json");
        if (!file.exists())
            save();
        try {
            freeLances = mapper.readValue(file, new TypeReference<ArrayList<FreeLance>>() {
            });
            System.out.println("FreeLance loaded : " + freeLances.size() + " detected !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean verif() {
        ArrayList<FreeLance> atRemove = new ArrayList<>();
        for (FreeLance freeLance : freeLances) {
            Message message = freeLance.getMessage().getMessage();
            if (Main.devarea.getMemberById(Snowflake.of(freeLance.getMemberId())).block() == null || message == null) {
                atRemove.add(freeLance);
                if (message != null) delete(false, message);
            }
        }

        if (atRemove.size() == 0)
            return false;

        freeLances.removeAll(atRemove);
        return true;
    }

    public static void save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("./freelance.json"), freeLances);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<FreeLance> getOf(Snowflake id) {
        ArrayList<FreeLance> newArray = new ArrayList<>();
        synchronized (freeLances) {
            for (int i = 0; i < freeLances.size(); i++) {
                if (freeLances.get(i).getMemberId().equals(id.asString()))
                    newArray.add(freeLances.get(i));
            }
        }
        return newArray;
    }
}
