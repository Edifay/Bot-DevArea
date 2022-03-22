package devarea.bot.automatical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.ConsumableCommand;
import devarea.bot.commands.object_for_stock.FreeLance;
import devarea.bot.commands.object_for_stock.MessageSeria;
import devarea.bot.commands.with_out_text_starter.CreateFreeLance;
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
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static devarea.bot.commands.Command.delete;
import static devarea.bot.event.FunctionEvent.startAway;

public class FreeLanceManager {

    private static final String localFile = "./freelance.json";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final long timeBetweenBump = 86400000L;

    public static Message message;
    private static ArrayList<FreeLance> freeLances = new ArrayList<>();

    private static TextChannel channel;

    private final static ArrayList<FreeLance> bumpedFreeLance = new ArrayList<>();

    public static void init() {
        try {
            load();
            channel = (TextChannel) Init.devarea.getChannelById(Init.idFreeLance).block();
            //if (!developing)
            Message msg = channel.getLastMessage().block();
            if (msg.getEmbeds().size() == 0 || msg.getEmbeds().get(0).getTitle().equals("Proposez vos services !"))
                sendLastMessage();
            else
                message = msg;


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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void update() {
        delete(false, message);
        sendLastMessage();
    }

    private static void sendLastMessage() {
        message = Command.send(channel, MessageCreateSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .description("Cliquez sur le bouton ci-dessous pour créer une page freelance !")
                                .color(ColorsUsed.same)
                                .title("Proposez vos services !")
                                .build())
                        .addComponent(ActionRow.of(Button.primary("createFreelance", "Créer une page Freelance")))
                        .build()
                , true);
    }

    public static boolean interact(ButtonInteractionEvent event) {
        if (event.getCustomId().equals("createFreelance")) {
            Member member = CommandManager.getMemberLogged(event.getInteraction().getMember().get());
            if (!FreeLanceManager.hasFreelance(member))
                CommandManager.addManualCommand(event.getInteraction().getMember().get(), new ConsumableCommand(CreateFreeLance.class) {
                    @Override
                    protected Command command() {
                        return new CreateFreeLance(this.member);
                    }
                });
            else
                event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Error !")
                                .description("Vous avez déjà une page FreeLance utilisez la commande `//freelance` pour effectuer des modifications.")
                                .color(ColorsUsed.wrong)
                                .build())
                        .ephemeral(true)
                        .build()).subscribe();
            return true;
        }
        return false;
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
            synchronized (freeLances) {
                if (!hasFreelance(id)) return false;
                FreeLance freeLance = FreeLanceManager.getFreelance(id);

                if (!bumpedFreeLance.contains(freeLance)) {
                    try {
                        delete(false, freeLance.getMessage().getMessage());
                    } catch (Exception e) {
                    }
                    freeLance.setMessage(new MessageSeria(Objects.requireNonNull(Command.send((TextChannel) Init.devarea.getChannelById(Init.idFreeLance).block(), MessageCreateSpec.builder()
                            .content("**Freelance de <@" + freeLance.getMemberId() + "> :**")
                            .addEmbed(freeLance.getEmbed())
                            .build(), true))));
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
        }
        return false;
    }


    public static void add(FreeLance mission) {
        synchronized (freeLances) {
            freeLances.add(mission);
            save();
        }
    }

    public static void remove(FreeLance mission) {
        synchronized (freeLances) {
            freeLances.remove(mission);
            save();
        }
    }

    private static void load() throws IOException {
        synchronized (freeLances) {
            File file = new File(localFile);
            if (!file.exists())
                save();
            freeLances = mapper.readValue(file, new TypeReference<>() {
            });
            System.out.println("FreeLance loaded : " + freeLances.size() + " detected !");
        }
    }

    public static boolean verif() {
        synchronized (freeLances) {
            ArrayList<FreeLance> atRemove = new ArrayList<>();
            for (FreeLance freeLance : freeLances)
                if (!Init.membersId.contains(Snowflake.of(freeLance.getMemberId()))) {
                   /* ((TextChannel) Init.devarea.getChannelById(Init.idFreeLance).block()).createMessage(messageCreateSpec -> {
                        messageCreateSpec.setContent("Le membre : <@" + freeLance.getMemberId() + "> est concidéré comme \"left\" ça missions devrait être supprimer !");
                    }).block();*/
                    /*atRemove.add(freeLance);
                    try {
                        delete(false, freeLance.getMessage().getMessage());
                    } catch (Exception e) {
                    }*/
                }

            if (atRemove.size() == 0)
                return false;

            freeLances.removeAll(atRemove);
            return true;
        }
    }

    public static void save() {
        synchronized (freeLances) {
            try {
                mapper.writeValue(new File(localFile), freeLances);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
