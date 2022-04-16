package devarea.bot.commands.with_out_text_starter;

import devarea.bot.Init;
import devarea.bot.commands.object_for_stock.MessageSeria;
import devarea.bot.automatical.MissionsManager;
import devarea.bot.commands.EndStape;
import devarea.bot.commands.FirstStape;
import devarea.bot.commands.LongCommand;
import devarea.bot.commands.Stape;
import devarea.bot.commands.object_for_stock.Mission;
import devarea.bot.data.ColorsUsed;
import devarea.bot.data.TextMessage;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;

import java.time.Instant;
import java.util.Objects;

import static devarea.bot.event.FunctionEvent.startAway;

public class CreateMission extends LongCommand {

    protected Mission mission;

    public CreateMission(final Member member) {
        super(member);
        this.mission = new Mission();
        this.mission.setMemberId(member.getId().asString());
        this.deletedCommand(10800000L);

        this.createLocalChannel("creation de mission", Init.idMissionsCategory);

        Stape niveauStape = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder().addEmbed(TextMessage.missionNiveau).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setNiveau(event.getMessage().getContent());
                mission.setMessage(
                        new MessageSeria(
                                Objects.requireNonNull(send((TextChannel) Init.devarea.getChannelById(Init.idMissionsPayantes).block(), MessageCreateSpec.builder()
                                        .content("**Mission proposée par <@" + mission.getMemberId() + "> :**")
                                        .addEmbed(EmbedCreateSpec.builder()
                                                .title(mission.getTitle())
                                                .description(mission.getDescriptionText() + "\n\nPrix: " + mission.getPrix() + "\nDate de retour: " + mission.getDateRetour() + "\nType de support: " + mission.getSupport() + "\nLangage: " + mission.getLangage() + "\nNiveau estimé: " + mission.getNiveau() + "\n\nCette mission est posté par : " + "<@" + member.getId().asString() + ">.")
                                                .color(ColorsUsed.just)
                                                .author(event.getMember().get().getDisplayName(), event.getMember().get().getAvatarUrl(), event.getMember().get().getAvatarUrl())
                                                .timestamp(Instant.now())
                                                .build())
                                        .addComponent(ActionRow.of(Button.secondary("took_mission", "Prendre la mission")))
                                        .build(), true))
                        )
                );
                MissionsManager.add(mission);
                MissionsManager.update();
                try {
                    startAway(() -> member.getPrivateChannel().block().createMessage(MessageCreateSpec.builder()
                            .addEmbed(EmbedCreateSpec.builder()
                                    .title("Suivis d'une mission")
                                    .description("La commande `//mission` permet de gérer sa mission, pour par exemple la supprimer.\n\n**Le site web** permet aussi de gérer ces missions dans l'onglet options : https://devarea.fr.")
                                    .color(ColorsUsed.same).build()).build()).block());
                } catch (Exception e) {
                }
                return end;
            }
        };

        Stape supportStape = new Stape(niveauStape) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder().addEmbed(TextMessage.missionSupport).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setSupport(event.getMessage().getContent());
                return callStape(0);
            }
        };

        Stape langageStape = new Stape(supportStape) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder().addEmbed(TextMessage.missionLangage).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setLangage(event.getMessage().getContent());
                return callStape(0);
            }
        };

        Stape dateRetourStape = new Stape(langageStape) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder().addEmbed(TextMessage.missionDate).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setDateRetour(event.getMessage().getContent());
                return callStape(0);
            }
        };

        Stape prixStage = new Stape(dateRetourStape) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder().addEmbed(TextMessage.missionPrix).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setPrix(event.getMessage().getContent());
                return callStape(0);
            }
        };

        Stape description = new Stape(prixStage) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder().addEmbed(TextMessage.missionDescription).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setDescriptionText(event.getMessage().getContent());
                return callStape(0);
            }
        };

        this.firstStape = new FirstStape(this.channel, description) {
            @Override
            public void onFirstCall(MessageCreateSpec spec) {
                super.onFirstCall(MessageCreateSpec.builder()
                        .content("<@" + member.getId().asString() + ">,")
                        .addEmbed(TextMessage.missionTitle).build());
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setTitle(event.getMessage().getContent());
                return callStape(0);
            }
        };
        this.lastMessage = this.firstStape.getMessage();
    }

}
