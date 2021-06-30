package devarea.bot.automatical;

import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.data.ColorsUsed;
import devarea.bot.data.TextMessage;
import devarea.bot.event.MemberJoin;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.Objects;
import java.util.function.Consumer;

public class Joining {

    private final Member member;
    private final TextChannel textChannel;
    private final Message message;
    private Boolean ended = false;

    private final ReactionEmoji no;
    private final ReactionEmoji yes;

    private int status;

    public Joining(final Member member) {
        this.member = member;
        this.status = 0;
        this.yes = ReactionEmoji.custom(Init.idYes);
        this.no = ReactionEmoji.custom(Init.idNo);
        System.out.println("Actualy before create");
        Init.devarea.getChannelById(Snowflake.of("843823896222629888")).block().addMemberOverwrite(member.getId(), PermissionOverwrite.forMember(member.getId(), PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL))).subscribe();
        this.textChannel = Init.devarea.createTextChannel(textChannelCreateSpec -> {
            textChannelCreateSpec.setName(member.getDisplayName());
            textChannelCreateSpec.setParentId(Init.idCategoryJoin);
            textChannelCreateSpec.setTopic("Petit questionnaire d'arrivé !");
        }).block();
        this.textChannel.addMemberOverwrite(member.getId(), PermissionOverwrite.forMember(member.getId(), PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY), PermissionSet.of(Permission.ADD_REACTIONS, Permission.SEND_MESSAGES))).subscribe();
        this.message = sendEmbed(embed -> {
            embed.setTitle("Bienvenue " + member.getDisplayName() + " sur Dev'Area !");
            embed.setDescription(TextMessage.firstText);
            embed.setColor(ColorsUsed.just);
        }, true);
        this.message.addReaction(this.yes).subscribe();

        new Thread(() -> {
            try {
                Thread.sleep(1000 * 60 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!this.ended) {
                try {
                    this.member.kick("Didn't finish QUIZZ").block();
                } catch (Exception ignored) {
                }
            }
        }).start();

    }

    final Object obj = new Object();

    public void next(final ReactionAddEvent event) {
        synchronized (obj) {
            if (!event.getMessageId().equals(this.message.getId()))
                return;

            boolean choice;
            if (event.getEmoji().equals(ReactionEmoji.custom(Init.idYes)) || event.getEmoji().equals(ReactionEmoji.custom(Init.idNo)))
                choice = event.getEmoji().equals(ReactionEmoji.custom(Init.idYes));
            else
                return;

            if (this.status == 0) {
                this.message.edit(msg -> msg.setEmbed(embed -> {
                    embed.setTitle("Pour quoi es-tu là ?");
                    embed.setDescription("    - Tu es développeur ou tu es ici pour apprendre à développer -> <:ayy:" + Init.idYes.getId().asString() + ">\n    - Tu es là car tu as besoin de développeurs, tu as une mission à donner -> <:ayy:" + Init.idNo.getId().asString() + ">");
                    embed.setColor(ColorsUsed.just);
                })).subscribe();
                this.message.addReaction(this.no).subscribe();
            } else if (this.status == 1) {

                if (choice)
                    this.message.edit(msg -> msg.setEmbed(embed -> {
                        embed.setTitle("Conseils de communications du code");
                        embed.setDescription(TextMessage.rulesForSpeakCode);
                        embed.setColor(ColorsUsed.just);
                    })).subscribe();

                else {
                    final PermissionOverwrite over = PermissionOverwrite.forMember(this.member.getId(), PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY), PermissionSet.of());
                    Init.devarea.getChannelById(Init.idMissionsGratuites).block().addMemberOverwrite(this.member.getId(), over).subscribe();
                    Init.devarea.getChannelById(Init.idMissionsPayantes).block().addMemberOverwrite(this.member.getId(), over).subscribe();

                    this.message.edit(msg -> msg.setEmbed(embed -> {
                        embed.setTitle("Conseils pour demander du code (missions)");
                        embed.setDescription(TextMessage.rulesForAskCode);
                        embed.setColor(ColorsUsed.just);
                    })).subscribe();
                }
                this.message.removeReaction(this.no, Init.client.getSelfId()).subscribe();

            } else if (this.status == 2) {
                this.message.edit(msg -> msg.setEmbed(embed -> {
                    embed.setTitle("Règles");
                    embed.setDescription(TextMessage.rules);
                    embed.setColor(ColorsUsed.just);
                })).subscribe();
            } else if (this.status == 3) {
                final PermissionOverwrite over = PermissionOverwrite.forMember(this.member.getId(), PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY), PermissionSet.of());
                Init.devarea.getChannelById(Init.idPresentation).block().addMemberOverwrite(this.member.getId(), over).subscribe();

                this.message.edit(msg -> msg.setEmbed(embed -> {
                    embed.setTitle("Avant de commencer !");
                    embed.setDescription(TextMessage.presentation);
                    embed.setColor(ColorsUsed.just);
                })).subscribe();

            } else {
                this.message.removeAllReactions().subscribe();

                final PermissionOverwrite over = PermissionOverwrite.forMember(this.member.getId(), PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY), PermissionSet.of());
                Init.devarea.getChannelById(Init.idRolesChannel).block().addMemberOverwrite(this.member.getId(), over).subscribe();

                this.message.edit(msg -> msg.setEmbed(embed -> {
                    embed.setTitle("Et le plus important...");
                    embed.setDescription(TextMessage.roles);
                    embed.setColor(ColorsUsed.just);
                })).subscribe();
                this.ended = true;
                new Thread(() -> {
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (this.member.getGuild().block() != null) {

                    }

                    this.member.addRole(Init.idRoleRulesAccepted).subscribe();
                    this.disconnect();

                    try {
                        this.member.getPrivateChannel().block().createEmbed(TextMessage.helpEmbed).subscribe();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ((TextChannel) Init.devarea.getChannelById(Init.idWelcomChannel).block()).createMessage(msg -> msg.setEmbed(embed -> {
                        embed.setTitle("Salut ! " + this.member.getTag() + ", bienvenue sur **Dev'Area**, amuse toi bien !");
                        embed.setDescription("Membre n°" + Init.devarea.getMembers().buffer().blockLast().size());
                        embed.setImage(this.member.getAvatarUrl());
                        embed.setColor(ColorsUsed.just);
                    })).subscribe();

                    ((TextChannel) Init.devarea.getChannelById(Init.idGeneralChannel).block())
                            .createMessage(msg -> msg
                                    .setContent("<@" + this.member.getId().asString() + "> a passé le petit questionnaire d'arrivée ! Vous pouvez lui souhaiter la bienvenue !"))
                            .subscribe();

                }).start();
            }

            try {
                Objects.requireNonNull(event.getMessage().block()).removeReaction(event.getEmoji(), event.getUserId()).subscribe();
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.status++;
        }
    }

    public void disconnect() {
        try {
            this.ended = true;
            MemberJoin.bindJoin.remove(this.member.getId());
            textChannel.delete().subscribe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Message send(final Consumer<? super MessageCreateSpec> spec, boolean block) {
        return Command.send(this.textChannel, spec, block);
    }

    protected Message sendEmbed(final Consumer<? super EmbedCreateSpec> spec, boolean block) {
        return send(msg -> msg.setEmbed(spec), block);
    }

}