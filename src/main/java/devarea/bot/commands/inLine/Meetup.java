package devarea.bot.commands.inLine;

import devarea.bot.commands.*;
import devarea.global.cache.ChannelCache;
import devarea.bot.presets.ColorsUsed;
import devarea.bot.presets.TextMessage;
import devarea.bot.Init;
import devarea.bot.automatical.MeetupHandler;
import devarea.bot.commands.commandTools.MeetupStock;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Meetup extends LongCommand implements SlashCommand {

    private MeetupStock meetup;
    private List<MeetupStock> canDelete;

    public Meetup(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        chatInteraction.deferReply().subscribe();

        Stape lastStape = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                MeetupHandler.addMeetupAtValide(meetup);
                editEmbed(TextMessage.meetupCreateAsk);
                delete(false, this.message);
                return end;
            }
        };

        Stape valide = new Stape(lastStape) {
            @Override
            protected boolean onCall(Message message) {
                setText(meetup.getEmbedVerif());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                if (event.getMessage().getContent().startsWith("yes")) return callStape(0);
                sendErrorEntry();
                return next;
            }
        };

        Stape image = new Stape(valide) {
            @Override
            protected boolean onCall(Message message) {
                setText(TextMessage.meetupCreateGetImage);
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                if (event.getMessage().getContent().startsWith("non"))
                    return callStape(0);
                else if (!event.getMessage().getAttachments().isEmpty()) {
                    meetup.setAttachment(((Attachment[]) event.getMessage().getAttachments().toArray(new Attachment[0]))[0].getUrl());
                    return callStape(0);
                }
                sendErrorEntry();
                return next;
            }
        };

        Stape getDate = new Stape(image) {
            @Override
            protected boolean onCall(Message message) {
                setText(TextMessage.meetupCreateGetDate);
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                try {
                    meetup.setDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(event.getMessage().getContent()));
                    return callStape(0);
                } catch (ParseException e) {
                    sendErrorEntry();
                }
                return next;
            }
        };

        Stape create = new Stape(getDate) {
            @Override
            protected boolean onCall(Message message) {
                setText(TextMessage.meetupCreateGetDescription);
                meetup = new MeetupStock();
                meetup.setAuthor(member.getId());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                if (!event.getMessage().getContent().isEmpty() && !event.getMessage().getContent().isBlank()) {
                    meetup.setName(event.getMessage().getContent());
                    return callStape(0);
                }
                sendErrorEntry();
                return next;
            }
        };

        Stape channel = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                editEmbed(EmbedCreateSpec.builder().title("Meetups !")
                        .color(ColorsUsed.just).timestamp(Instant.now())
                        .description("Voici le channel des meetups : <#" + Init.initial.meetupAnnounce_channel.asString() + ">")
                        .build());
                delete(false, this.message);
                return end;
            }
        };

        Stape endDelete = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                editEmbed(EmbedCreateSpec.builder().title("Le meetup a bien été supprimé !").color(ColorsUsed.just).timestamp(Instant.now()).build());
                delete(false, this.message);
                return end;
            }
        };

        Stape delete = new Stape(endDelete) {
            @Override
            protected boolean onCall(Message test) {
                canDelete = MeetupHandler.getMeetupsFrom(member.getId());
                AtomicInteger a = new AtomicInteger();
                canDelete.forEach(meetupStock -> {
                    Command.deletedMessage((TextChannel) ChannelCache.watch(test.getChannelId().asString()),
                            meetupStock.getEmbed().content("**" + a.get() + ":**").build());
                    a.getAndIncrement();
                });

                setText(EmbedCreateSpec.builder().title("Meetup à delete...").description("Vous allez voir la liste " +
                        "de tout vos meetup s'afficher. Envoyer son numéro attribué pour le supprimer.").footer("Vous" +
                        " pouvez annuler | cancel", null).color(ColorsUsed.just).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                try {
                    int number = Integer.parseInt(event.getMessage().getContent());
                    if (number >= 0 && number < canDelete.size()) {
                        MeetupHandler.remove(canDelete.get(number));
                        return callStape(0);
                    } else {
                        sendErrorEntry();
                        return next;
                    }
                } catch (Exception e) {
                }
                sendErrorEntry();
                return next;
            }
        };

        this.firstStape = new FirstStape(this.channel, create, delete, channel) {
            @Override
            public void onFirstCall(MessageCreateSpec spec) {
                super.onFirstCall(MessageCreateSpec.builder().addEmbed(TextMessage.meetupCommandExplain).build());
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                if (event.getMessage().getContent().startsWith("create"))
                    return callStape(0);
                else if (event.getMessage().getContent().startsWith("delete"))
                    return callStape(1);
                else if (event.getMessage().getContent().startsWith("channel"))
                    return callStape(2);
                else
                    sendErrorEntry();

                return next;
            }
        };
        this.lastMessage = this.firstStape.getMessage();
    }

    public Meetup() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("meetup")
                .description("Permet de créer et de gérer ses meetups possédés.")
                .build();
    }
}
