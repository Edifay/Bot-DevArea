package devarea.bot.commands.with_out_text_starter;

import devarea.bot.Init;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.FirstStape;
import devarea.bot.commands.LongCommand;
import devarea.bot.commands.Stape;
import devarea.bot.data.ColorsUsed;
import devarea.bot.data.TextMessage;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.function.Consumer;

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

        this.createLocalChannel(member.getDisplayName(), Init.idCategoryJoin);

        Stape roles = new Stape() {
            @Override
            protected boolean onCall(Message message) {
                removeAllEmoji();
                final PermissionOverwrite over = PermissionOverwrite.forMember(member.getId(), PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY), PermissionSet.of());
                startAway(() -> Init.devarea.getChannelById(Init.idRolesChannel).block().addMemberOverwrite(member.getId(), over).subscribe());
                setText(embed -> {
                    embed.setTitle("Et le plus important...");
                    embed.setDescription(TextMessage.roles);
                    embed.setColor(ColorsUsed.just);
                });

                new Thread(() -> {
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (Init.membersId.contains(member.getId())) {
                        member.addRole(Init.idRoleRulesAccepted).subscribe();
                        endCommand();

                        try {
                            startAway(() -> member.getPrivateChannel().block().createEmbed(TextMessage.helpEmbed).subscribe());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        startAway(() -> ((TextChannel) Init.devarea.getChannelById(Init.idWelcomChannel).block()).createMessage(msg -> msg.setEmbed(embed -> {
                            embed.setTitle("Salut ! " + member.getTag() + ", bienvenue sur **Dev'Area**, amuse toi bien !");
                            embed.setDescription("Membre n°" + Init.membersId.size());
                            embed.setImage(member.getAvatarUrl());
                            embed.setColor(ColorsUsed.just);
                        })).subscribe());


                        ((TextChannel) Init.devarea.getChannelById(Init.idGeneralChannel).block())
                                .createMessage(msg -> msg
                                        .setContent("<@" + member.getId().asString() + "> a passé le petit questionnaire d'arrivée ! Vous pouvez lui souhaiter la bienvenue !"))
                                .subscribe();
                    }
                }).start();
                return false;
            }
        };

        Stape presentation = new Stape(roles) {
            @Override
            protected boolean onCall(Message message) {
                final PermissionOverwrite over = PermissionOverwrite.forMember(member.getId(), PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY), PermissionSet.of());
                startAway(() -> Init.devarea.getChannelById(Init.idPresentation).block().addMemberOverwrite(member.getId(), over).subscribe());
                setText(embed -> {
                    embed.setTitle("Avant de commencer !");
                    embed.setDescription(TextMessage.presentation);
                    embed.setColor(ColorsUsed.just);
                });
                return false;
            }

            @Override
            protected boolean onReceiveReact(ReactionAddEvent event) {
                if (isYes(event))
                    return callStape(0);
                return super.onReceiveReact(event);
            }
        };

        Stape rules = new Stape(presentation) {
            @Override
            protected boolean onCall(Message message) {
                setText(embed -> {
                    embed.setTitle("Règles");
                    embed.setDescription(TextMessage.rules);
                    embed.setColor(ColorsUsed.just);
                });
                return false;
            }

            @Override
            protected boolean onReceiveReact(ReactionAddEvent event) {
                if (isYes(event))
                    return callStape(0);
                return super.onReceiveReact(event);
            }
        };

        Stape needDevInfo = new Stape(rules) {
            @Override
            protected boolean onCall(Message message) {
                removeNoEmoji();
                final PermissionOverwrite over = PermissionOverwrite.forMember(member.getId(), PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY), PermissionSet.of());
                startAway(() -> Init.devarea.getChannelById(Init.idMissionsGratuites).block().addMemberOverwrite(member.getId(), over).subscribe());
                startAway(() -> Init.devarea.getChannelById(Init.idMissionsPayantes).block().addMemberOverwrite(member.getId(), over).subscribe());
                startAway(() -> Init.devarea.getChannelById(Snowflake.of("856081355309842452")).block().addMemberOverwrite(member.getId(), over).subscribe());
                setText(embed -> {
                    embed.setTitle("Conseils pour demander du code (missions)");
                    embed.setDescription(TextMessage.rulesForAskCode);
                    embed.setColor(ColorsUsed.just);
                });
                return false;
            }

            @Override
            protected boolean onReceiveReact(ReactionAddEvent event) {
                if (isYes(event))
                    return callStape(0);
                return super.onReceiveReact(event);
            }
        };

        Stape devInfo = new Stape(rules) {
            @Override
            protected boolean onCall(Message message) {
                removeNoEmoji();
                setText(embed -> {
                    embed.setTitle("Conseils de communications du code");
                    embed.setDescription(TextMessage.rulesForSpeakCode);
                    embed.setColor(ColorsUsed.just);
                });
                return false;
            }

            @Override
            protected boolean onReceiveReact(ReactionAddEvent event) {
                if (isYes(event))
                    return callStape(0);
                return super.onReceiveReact(event);
            }
        };

        Stape DevOrNeedDev = new Stape(devInfo, needDevInfo) {
            @Override
            protected boolean onCall(Message message) {
                setText(embed -> {
                    embed.setTitle("Pour quoi es-tu là ?");
                    embed.setDescription("    - Tu es développeur ou tu es ici pour apprendre à développer -> <:ayy:" + Init.idYes.getId().asString() + ">\n    - Tu es là car tu as besoin de développeurs, tu as une mission à donner -> <:ayy:" + Init.idNo.getId().asString() + ">");
                    embed.setColor(ColorsUsed.just);
                });
                addNoEmoji();
                return next;
            }

            @Override
            protected boolean onReceiveReact(ReactionAddEvent event) {
                if (isYes(event)) {
                    return callStape(0);
                } else if (isNo(event)) {
                    return callStape(1);
                }
                return super.onReceiveReact(event);
            }
        };

        this.firstStape = new FirstStape(this.channel, DevOrNeedDev) {
            @Override
            public void onFirstCall(Consumer<? super MessageCreateSpec> deleteThisVariableAndSetYourOwnMessage) {
                startAway(() -> Init.devarea.getChannelById(Snowflake.of("843823896222629888")).block().addMemberOverwrite(member.getId(), PermissionOverwrite.forMember(member.getId(), PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL))).subscribe());
                super.onFirstCall(msg -> msg.setEmbed(embed -> {
                    embed.setTitle("Bienvenue " + member.getDisplayName() + " sur Dev'Area !");
                    embed.setDescription(TextMessage.firstText);
                    embed.setColor(ColorsUsed.just);
                }));
                addYesEmoji();
            }

            @Override
            protected boolean onReceiveReact(ReactionAddEvent event) {
                if (isYes(event))
                    return callStape(0);
                return super.onReceiveReact(event);
            }
        };
        this.lastMessage = this.firstStape.getMessage();
    }

}
