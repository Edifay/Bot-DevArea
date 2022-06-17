package devarea.bot.commands.outLine;

import devarea.Main;
import devarea.backend.controllers.rest.requestContent.RequestHandlerAuth;
import devarea.bot.cache.ChannelCache;
import devarea.bot.cache.MemberCache;
import devarea.bot.Init;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.FirstStape;
import devarea.bot.commands.LongCommand;
import devarea.bot.commands.Stape;
import devarea.bot.presets.ColorsUsed;
import devarea.bot.presets.TextMessage;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.ArrayList;

import static devarea.bot.event.FunctionEvent.startAway;

public class JoinCommand extends LongCommand {

    public JoinCommand(Member member) {
        super(member);
        new Thread(() -> {
            try {
                Thread.sleep(600000L);
            } catch (InterruptedException e) {
            } finally {
                if (CommandManager.hasCommand(this)) {
                    this.member.kick("Didn't finish QCM !").subscribe();
                }
            }
        }).start();


        this.createLocalChannel(
                member.getDisplayName(),
                Init.initial.join_category,
                false);

        Stape roles = new Stape() {
            @Override
            protected boolean onCall(Message message) {
                final PermissionOverwrite over = PermissionOverwrite.forMember(member.getId(),
                        PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY), PermissionSet.of());
                startAway(() -> ((TextChannel) ChannelCache.watch(Init.initial.roles_channel.asString())).addMemberOverwrite(member.getId(), over).subscribe());
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Et le plus important...")
                                .description(TextMessage.roles)
                                .color(ColorsUsed.just).build())
                        .components(getEmptyButton())
                        .build());

                new Thread(() -> {
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (MemberCache.contain(member.getId().asString())) {
                        member.addRole(Init.initial.rulesAccepted_role).subscribe();
                        endCommand();

                        try {
                            PrivateChannel privateChannel = member.getPrivateChannel().block();
                            startAway(() -> {
                                privateChannel.createMessage(TextMessage.helpEmbed).block();

                                final String code = RequestHandlerAuth.getCodeForMember(member.getId().asString());

                                final Message message_at_edit = privateChannel.createMessage(MessageCreateSpec.builder()
                                        .addEmbed(EmbedCreateSpec.builder()
                                                .title("Authentification au site de Dev'area !")
                                                .description("Vous venez de vous authentifier sur le site de dev'area" +
                                                        " !\n\nPour vous connecter utilisez ce lien :\n\n" + Main.domainName + "?code" +
                                                        "=" + code + "\n\nCe message sera supprimé d'ici **5 " +
                                                        "minutes** pour sécuriser l'accès. Si vous avez besoin de le " +
                                                        "retrouver exécutez de nouveau la commande !")
                                                .color(ColorsUsed.just)
                                                .build())
                                        .build()).block();

                                final ArrayList<EmbedCreateSpec> embeds = new ArrayList<>();

                                embeds.add(EmbedCreateSpec.builder()
                                        .title("Authentification au site de Dev'area !")
                                        .description("Si vous voulez retrouver le lien d'authentification vous pouvez" +
                                                " " +
                                                "exécuter la commande " +
                                                "`//auth` à nouveau !")
                                        .color(ColorsUsed.same)
                                        .build());

                                startAway(() -> {
                                    try {
                                        Thread.sleep(300000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } finally {
                                        message_at_edit.edit(MessageEditSpec.builder()
                                                .embeds(embeds)
                                                .build()).subscribe();
                                    }
                                });

                            });

                        } catch (Exception e) {
                        }

                        startAway(() -> ((TextChannel) ChannelCache.watch(Init.initial.welcome_channel.asString())).createMessage(MessageCreateSpec.builder()
                                .addEmbed(EmbedCreateSpec.builder()
                                        .title("Salut ! " + member.getTag() + ", bienvenue sur **Dev'Area**, amuse " +
                                                "toi bien !")
                                        .description("Membre n°" + MemberCache.cacheSize())
                                        .image(member.getAvatarUrl())
                                        .color(ColorsUsed.just)
                                        .build())
                                .build()).subscribe());

                        ((TextChannel) ChannelCache.watch(Init.initial.general_channel.asString()))
                                .createMessage(msg -> msg
                                        .setContent("<@" + member.getId().asString() + "> a passé le petit " +
                                                "questionnaire d'arrivée ! Vous pouvez lui souhaiter la bienvenue !"))
                                .subscribe();
                    }
                }).start();
                return false;
            }
        };

        Stape presentation = new Stape(roles) {
            @Override
            protected boolean onCall(Message message) {
                final PermissionOverwrite over = PermissionOverwrite.forMember(member.getId(),
                        PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY), PermissionSet.of());
                startAway(() -> ((TextChannel) ChannelCache.watch(Init.initial.presentation_channel.asString())).addMemberOverwrite(member.getId(), over).subscribe());
                setText(EmbedCreateSpec.builder()
                        .title("Avant de commencer !")
                        .description(TextMessage.presentation)
                        .color(ColorsUsed.just).build());
                return false;
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (isYes(event))
                    return callStape(0);
                return super.onReceiveInteract(event);
            }
        };

        Stape rules = new Stape(presentation) {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder()
                        .title("Règles")
                        .description(TextMessage.rules)
                        .color(ColorsUsed.just).build());
                return false;
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (isYes(event))
                    return callStape(0);
                return super.onReceiveInteract(event);
            }
        };

        Stape needDevInfo = new Stape(rules) {
            @Override
            protected boolean onCall(Message message) {
                final PermissionOverwrite over = PermissionOverwrite.forMember(member.getId(),
                        PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY), PermissionSet.of());
                startAway(() -> ((TextChannel) ChannelCache.watch(Init.initial.freeMissions_channel.asString())).addMemberOverwrite(member.getId(), over).subscribe());
                startAway(() -> ((TextChannel) ChannelCache.watch(Init.initial.paidMissions_channel.asString())).addMemberOverwrite(member.getId(), over).subscribe());
                startAway(() -> ((TextChannel) ChannelCache.watch(Init.initial.freelance_channel.asString())).addMemberOverwrite(member.getId(), over).subscribe());
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Conseils pour demander du code (missions)")
                                .description(TextMessage.rulesForAskCode)
                                .color(ColorsUsed.just).build())
                        .addComponent(getYesButton())
                        .build());
                return false;
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (isYes(event))
                    return callStape(0);
                return super.onReceiveInteract(event);
            }
        };

        Stape devInfo = new Stape(rules) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Conseils de communications du code")
                                .description(TextMessage.rulesForSpeakCode)
                                .color(ColorsUsed.just).build())
                        .addComponent(getYesButton())
                        .build());
                return false;
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (isYes(event))
                    return callStape(0);
                return super.onReceiveInteract(event);
            }
        };

        Stape DevOrNeedDev = new Stape(devInfo, needDevInfo) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Pour quoi es-tu là ?")
                                .description("    - Tu es développeur ou tu es ici pour apprendre à développer -> " +
                                        "<:ayy:" + Init.idYes.getId().asString() + ">\n    - Tu es là car tu as " +
                                        "besoin de développeurs, tu as une mission à donner -> <:ayy:" + Init.idNo.getId().asString() + ">")
                                .color(ColorsUsed.just).build())
                        .addComponent(getYesNoButton())
                        .build());
                return next;
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (isYes(event)) {
                    return callStape(0);
                } else if (isNo(event)) {
                    return callStape(1);
                }
                return super.onReceiveInteract(event);
            }
        };

        this.firstStape = new FirstStape(this.channel, DevOrNeedDev) {
            @Override
            public void onFirstCall(MessageCreateSpec deleteThisVariableAndSetYourOwnMessage) {
                startAway(() -> ((TextChannel) ChannelCache.watch("843823896222629888")).addMemberOverwrite(member.getId(), PermissionOverwrite.forMember(member.getId(), PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL))).subscribe());
                super.onFirstCall(MessageCreateSpec.builder().addEmbed(EmbedCreateSpec.builder()
                                .title("Bienvenue " + member.getDisplayName() + " sur Dev'Area !")
                                .description(TextMessage.firstText)
                                .color(ColorsUsed.just).build())
                        .addComponent(getYesButton())
                        .build());
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (isYes(event))
                    return callStape(0);
                return super.onReceiveInteract(event);
            }
        };
        this.lastMessage = this.firstStape.getMessage();
    }

}
