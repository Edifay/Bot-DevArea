package devarea.bot.automatical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.ConsumableCommand;
import devarea.bot.commands.object_for_stock.FreeLance;
import devarea.bot.commands.with_out_text_starter.CreateFreeLance;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.http.client.ClientException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static devarea.bot.commands.Command.delete;
import static devarea.bot.event.FunctionEvent.startAway;

public class FreeLanceManager {

    private static final String localFile = "./freelance.json";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final long timeBetweenBump = 86400000L;

    public static Message message;
    private static ArrayList<FreeLance> freeLances = new ArrayList<>();

    private final static ArrayList<FreeLance> bumpedFreeLance = new ArrayList<>();

    public static void init() {
        try {
            load();
            sendLastMessage();
            new Thread(() -> {
                if (verif())
                    save();
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void update() {
        delete(false, message);
        sendLastMessage();
    }

    private static void sendLastMessage() {
        message = Command.sendEmbed((TextChannel) Init.devarea.getChannelById(Init.idFreeLance).block(), embedCreateSpec -> {
            embedCreateSpec.setColor(ColorsUsed.same);
            embedCreateSpec.setTitle("Proposez vos services !");
            embedCreateSpec.setDescription("Cliquez sur <:ayy:" + Init.idYes.getId().asString() + "> pour créer une page de compétances !");
            embedCreateSpec.setFooter("Cette fonctionnalité est en avant première, si vous voyez le moindre bug, veuillez nous alerter !", null);
        }, true);
        message.addReaction(ReactionEmoji.custom(Init.idYes)).subscribe();
    }

    public static void react(ReactionAddEvent event) {
        if (event.getMessageId().equals(message.getId())) {
            startAway(() -> event.getMessage().block().removeReaction(event.getEmoji(), event.getUserId()).subscribe());

            if (event.getEmoji().equals(ReactionEmoji.custom(Init.idYes)))
                if (!FreeLanceManager.hasFreelance(event.getMember().get()))
                    CommandManager.addManualCommand(event.getMember().get().getId(), new ConsumableCommand() {
                        @Override
                        protected Command command() {
                            return new CreateFreeLance(event);
                        }
                    });
                else
                    Command.sendError((TextChannel) event.getChannel().block(), "Vous avez déjà une page FreeLance utilisez la commande `//freelance` pour effectuer des modifications.");

        }
    }

    public static boolean hasFreelance(String id) {
        synchronized (freeLances) {
            for (FreeLance freeLance : freeLances)
                if (freeLance.getMemberId().equals(id)) return true;
        }
        return false;
    }

    public static boolean hasFreelance(Member member) {
        return hasFreelance(member.getId().asString());
    }

    public static FreeLance getFreelance(String id) {
        synchronized (freeLances) {
            for (FreeLance freeLance : freeLances)
                if (freeLance.getMemberId().equals(id))
                    return freeLance;
            return null;
        }
    }

    public static FreeLance getFreelance(Member member) {
        return getFreelance(member.getId().asString());
    }

    public static boolean bumpFreeLance(String id) {
        synchronized (bumpedFreeLance) {
            if (!hasFreelance(id)) return false;
            FreeLance freeLance = FreeLanceManager.getFreelance(id);

            if (!bumpedFreeLance.contains(freeLance)) {
                delete(false, freeLance.getMessage().getMessage());
                freeLance.setMessage(new MessageSeria(Command.sendEmbed((TextChannel) Init.devarea.getChannelById(Init.idFreeLance).block(), freeLance.getEmbed(), true)));
                update();
                save();
                bumpedFreeLance.add(freeLance);
                new Thread(() -> {
                    try {
                        Thread.sleep(timeBetweenBump);
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
        return false;
    }


    public static void stop() {
        delete(true, message);
    }

    public static void add(FreeLance mission) {
        freeLances.add(mission);
        save();
    }

    public static void remove(FreeLance mission) {
        freeLances.remove(mission);
        save();
    }

    private static void load() throws IOException {
        File file = new File(localFile);
        if (!file.exists())
            save();
        freeLances = mapper.readValue(file, new TypeReference<>() {
        });
        System.out.println("FreeLance loaded : " + freeLances.size() + " detected !");
    }

    public static boolean verif() {
        ArrayList<FreeLance> atRemove = new ArrayList<>();
        for (FreeLance freeLance : freeLances) {

            Message message = null;
            try {
                message = freeLance.getMessage().getMessage();
            } catch (ClientException ignored) {
            }

            if (!Init.membersId.contains(Snowflake.of(freeLance.getMemberId())) || message == null) {
                atRemove.add(freeLance);
                if (message != null)
                    delete(false, message);
            }
        }

        if (atRemove.size() == 0)
            return false;

        freeLances.removeAll(atRemove);
        return true;
    }

    public static void save() {
        try {
            mapper.writeValue(new File(localFile), freeLances);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
