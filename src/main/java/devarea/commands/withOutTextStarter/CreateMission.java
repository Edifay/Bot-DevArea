package devarea.commands.withOutTextStarter;

import devarea.Data.ColorsUsed;
import devarea.Data.TextMessage;
import devarea.Main;
import devarea.automatical.MissionsManager;
import devarea.commands.ExternalLongCommand;
import devarea.commands.FirstStape;
import devarea.commands.Stape;
import devarea.event.EndStape;
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

public class CreateMission extends ExternalLongCommand {

    protected String title;
    protected String descriptionText;
    protected String prix;
    protected String dateRetour;
    protected String langage;
    protected String support;
    protected String niveau;

    public CreateMission(ReactionAddEvent event) {
        super(event);
        this.deletedCommand(1200000);
        this.channel = Main.devarea.createTextChannel(textChannelCreateSpec -> {
            textChannelCreateSpec.setName("creation de mission");
            textChannelCreateSpec.setParentId(Main.idMissionsCategory);
        }).block();
        this.channel.addRoleOverwrite(Main.idRoleRulesAccepted,
                PermissionOverwrite.forRole(Main.idRoleRulesAccepted,
                        PermissionSet.of(),
                        PermissionSet.of(Permission.VIEW_CHANNEL)))
                .block();
        this.channel.addMemberOverwrite(this.member.getId(),
                PermissionOverwrite.forMember(this.member.getId(),
                        PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY, Permission.SEND_MESSAGES),
                        PermissionSet.of(Permission.ADD_REACTIONS)))
                .block();

        Stape niveauStape = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                setMessage(messageEditSpec -> messageEditSpec.setEmbed(TextMessage.missionNiveau));
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                niveau = event.getMessage().getContent();
                sendEmbed((TextChannel) Main.devarea.getChannelById(Main.idMissionsPayantes).block(), embedCreateSpec -> {
                    embedCreateSpec.setTitle(title);
                    embedCreateSpec.setDescription(descriptionText +
                            "\n\nPrix: " + prix + "\nDate de retour: " + dateRetour + "\nType de support: " + support + "\nLangage: " + langage + "\nNiveau estimé: " + niveau + "\n\nCette mission est posté par : " + "<@" + member.getId().asString() + ">.");
                    embedCreateSpec.setColor(ColorsUsed.just);
                    embedCreateSpec.setAuthor(event.getMember().get().getDisplayName(), event.getMember().get().getAvatarUrl(), event.getMember().get().getAvatarUrl());
                    embedCreateSpec.setTimestamp(Instant.now());
                });
                MissionsManager.update();
                endCommand();
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
                support = event.getMessage().getContent();
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
                langage = event.getMessage().getContent();
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
                dateRetour = event.getMessage().getContent();
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
                prix = event.getMessage().getContent();
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
                descriptionText = event.getMessage().getContent();
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
                title = event.getMessage().getContent();
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
