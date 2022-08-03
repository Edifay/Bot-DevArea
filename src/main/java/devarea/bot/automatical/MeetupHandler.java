package devarea.bot.automatical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.commands.commandTools.MeetupStock;
import devarea.bot.commands.commandTools.MessageSeria;
import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.VoiceChannelCreateSpec;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static devarea.bot.commands.Command.send;
import static devarea.global.utils.ThreadHandler.startAway;

public class MeetupHandler {


    public static ArrayList<MeetupStock> meetups = new ArrayList<>();

    public static void init() {
        load();
        System.out.println("Meetups loaded : " + meetups.size() + " detected !");
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(60000);
                    Date date = new Date();
                    meetups.forEach(meetupStock -> {
                        if (!meetupStock.getAlreadyMade()) {
                            if (date.after(meetupStock.getDate())) {
                                send((TextChannel) ChannelCache.watch(Init.initial.meetupAnnounce_channel.asString()),
                                        MessageCreateSpec.builder()
                                                .content("Un meetup a commencé avec sujet :\n\n " + meetupStock.getName() + "\n\n<@&" + Init.initial.pingMeetup_role.asString() + ">").build()
                                        , false);
                                Init.devarea.createVoiceChannel(VoiceChannelCreateSpec.builder()
                                        .parentId(Init.initial.general_category)
                                        .name("Meetup by " + MemberCache.get(meetupStock.getAuthor().asString()).getDisplayName())
                                        .build()).subscribe();
                                meetupStock.setAlreadyMade(true);
                                save();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void load() {
        File file = new File("meetup.json");
        if (!file.exists())
            save();
        try {
            ObjectMapper mapper = new ObjectMapper();
            meetups = mapper.readValue(file,
                    new TypeReference<>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("meetup.json"), meetups);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addMeetupAtValide(MeetupStock meetup) {
        Message message = send((TextChannel) ChannelCache.watch(Init.initial.meetupVerif_channel.asString()),
                meetup.getEmbed().build(), true);
        meetup.setMessage(new MessageSeria(message));
        meetups.add(meetup);
        addYesAndNo(message);
        save();
    }

    public static void addYesAndNo(Message message) {
        message.addReaction(ReactionEmoji.custom(Init.idYes)).subscribe();
        message.addReaction(ReactionEmoji.custom(Init.idNo)).subscribe();
    }

    public static void addYes(Message message) {
        message.addReaction(ReactionEmoji.custom(Init.idYes)).subscribe();
    }

    public static boolean getEvent(ReactionAddEvent event) {
        if (!event.getChannelId().equals(Init.initial.meetupVerif_channel))
            return false;
        for (MeetupStock meetup : meetups) {
            if (meetup.getMessage().getMessageID().equals(event.getMessageId())) {
                if (event.getEmoji().equals(ReactionEmoji.custom(Init.idYes))) {
                    try {
                        startAway(() -> MemberCache.get(meetup.getAuthor().asString()).getPrivateChannel().block().createMessage(EmbedCreateSpec.builder()
                                .title("Votre meetup a été accepté !")
                                .timestamp(Instant.now())
                                .color(ColorsUsed.just)
                                .build()).subscribe());

                    } catch (Exception e) {
                    }

                    startAway(() -> Command.delete(false, meetup.getMessage().getMessage()));

                    Message message =
                            send((TextChannel) ChannelCache.watch(Init.initial.meetupAnnounce_channel.asString()),
                                    meetup.getEmbed().build(), true);
                    addYes(message);
                    meetup.setMessage(new MessageSeria(message));
                } else if (event.getEmoji().equals(ReactionEmoji.custom(Init.idNo))) {
                    try {
                        MemberCache.get(meetup.getAuthor().asString()).getPrivateChannel().block().createMessage(EmbedCreateSpec.builder()
                                .title("Votre meetup a été rejeté !")
                                .timestamp(Instant.now())
                                .color(ColorsUsed.wrong)
                                .build()).block();
                    } catch (Exception e) {
                    }
                    meetups.remove(meetup);
                }
                save();
                return true;
            }
        }
        return false;
    }

    public static List<MeetupStock> getMeetupsFrom(Snowflake memberID) {
        List<MeetupStock> list = new ArrayList<>();
        meetups.forEach(meetupStock -> {
            if (meetupStock.getAuthor().equals(memberID))
                list.add(meetupStock);
        });
        meetups.forEach(meetupStock -> {
            if (meetupStock.getAuthor().equals(memberID))
                list.add(meetupStock);
        });
        return list;
    }

    public static void remove(MeetupStock meetup) {
        for (MeetupStock value : meetups) {
            if (value.equals(meetup)) {
                Command.delete(false, meetup.getMessage().getMessage());
                meetups.remove(meetup);
                save();
            }
        }
    }
}
