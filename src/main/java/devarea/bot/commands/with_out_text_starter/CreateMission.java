package devarea.bot.commands.with_out_text_starter;

import devarea.bot.automatical.MessageSeria;
import devarea.bot.commands.LongCommand;
import devarea.bot.commands.object_for_stock.Mission;
import devarea.bot.data.ColorsUsed;
import devarea.bot.data.TextMessage;
import devarea.bot.Init;
import devarea.bot.automatical.MissionsManager;
import devarea.bot.commands.FirstStape;
import devarea.bot.commands.Stape;
import devarea.bot.commands.EndStape;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.time.Instant;
import java.util.function.Consumer;

public class CreateMission extends LongCommand {

    protected Mission mission;

    public CreateMission(ReactionAddEvent event) {
        super(event);
        this.mission = new Mission();
        this.deletedCommand(10800000L);
        this.channel = Init.devarea.createTextChannel(textChannelCreateSpec -> {
            textChannelCreateSpec.setName("creation de mission");
            textChannelCreateSpec.setParentId(Init.idMissionsCategory);
        }).block();
        assert this.channel != null;
        this.channel.addRoleOverwrite(Init.idRoleRulesAccepted,
                PermissionOverwrite.forRole(Init.idRoleRulesAccepted,
                        PermissionSet.of(),
                        PermissionSet.of(Permission.VIEW_CHANNEL)))
                .subscribe();
        this.channel.addMemberOverwrite(this.member.getId(),
                PermissionOverwrite.forMember(this.member.getId(),
                        PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY, Permission.SEND_MESSAGES),
                        PermissionSet.of(Permission.ADD_REACTIONS)))
                .subscribe();

        Stape niveauStape = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                setMessage(messageEditSpec -> messageEditSpec.setEmbed(TextMessage.missionNiveau));
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setNiveau(event.getMessage().getContent());
                mission.setMessage(
                        new MessageSeria(
                                sendEmbed((TextChannel) Init.devarea.getChannelById(Init.idMissionsPayantes).block(), embedCreateSpec -> {
                                    embedCreateSpec.setTitle(mission.getTitle());
                                    embedCreateSpec.setDescription(mission.getDescriptionText() +
                                            "\n\nPrix: " + mission.getPrix() + "\nDate de retour: " + mission.getDateRetour() + "\nType de support: " + mission.getSupport() + "\nLangage: " + mission.getLangage() + "\nNiveau estimé: " + mission.getNiveau() + "\n\nCette mission est posté par : " + "<@" + member.getId().asString() + ">.");
                                    embedCreateSpec.setColor(ColorsUsed.just);
                                    embedCreateSpec.setAuthor(event.getMember().get().getDisplayName(), event.getMember().get().getAvatarUrl(), event.getMember().get().getAvatarUrl());
                                    embedCreateSpec.setTimestamp(Instant.now());
                                }, true)
                        )
                );
                MissionsManager.add(mission);
                MissionsManager.update();
                return end;
            }
        };

        Stape supportStape = new Stape(niveauStape) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(messageEditSpec -> messageEditSpec.setEmbed(TextMessage.missionSupport));
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
                setMessage(messageEditSpec -> messageEditSpec.setEmbed(TextMessage.missionLangage));
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
                setMessage(messageEditSpec -> messageEditSpec.setEmbed(TextMessage.missionDate));
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
                setMessage(messageEditSpec -> messageEditSpec.setEmbed(TextMessage.missionPrix));
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
                setMessage(messageEditSpec -> messageEditSpec.setEmbed(TextMessage.missionDescription));
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
            public void onFirstCall(Consumer<? super MessageCreateSpec> spec) {
                super.onFirstCall(messageCreateSpec -> {
                    messageCreateSpec.setEmbed(TextMessage.missionTitle);
                    messageCreateSpec.setContent("<@" + member.getId().asString() + ">,");
                });
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                mission.setTitle(event.getMessage().getContent());
                mission.setMemberId(event.getMember().get().getId().asString());
                return callStape(0);
            }
        };
    }

    @Override
    protected Boolean endCommand() {
        try {
            this.channel.delete().block();
        } catch (Exception e) {
        }
        this.ended = true;
        return super.endCommand();
    }

}
