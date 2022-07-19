package devarea.bot.commands;

import devarea.bot.Init;
import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.ChannelCache;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.*;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.HashSet;
import java.util.Set;

public abstract class Command {

    protected final Member member;
    protected TextChannel channel;
    protected ChatInputInteractionEvent chatInteraction;

    public Command() {
        this.member = null;
    }

    public Command(final Member member) {
        this.member = member;
    }

    public Command(final Member member, final TextChannel channel) {
        this.member = member;
        this.channel = channel;
    }

    public Command(final Member member, final ChatInputInteractionEvent chatInteraction) {
        this.member = member;
        this.channel = (TextChannel) ChannelCache.get(chatInteraction.getInteraction().getChannelId().asString());
        this.chatInteraction = chatInteraction;
    }

    protected Boolean endCommand() {
        CommandManager.removeCommand(this.member.getId(), this);
        return true;
    }


    protected boolean createLocalChannel(final String name, final Snowflake parentId, final boolean canWrite) {
        Set<PermissionOverwrite> set = new HashSet<>();
        set.add(PermissionOverwrite.forRole(Init.initial.rulesAccepted_role,
                PermissionSet.of(),
                PermissionSet.of(Permission.VIEW_CHANNEL)));
        set.add(PermissionOverwrite.forMember(this.member.getId(),
                canWrite ?
                        PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY,
                                Permission.SEND_MESSAGES)
                        :
                        PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY),
                PermissionSet.of(Permission.ADD_REACTIONS)));
        this.channel = Init.devarea.createTextChannel(TextChannelCreateSpec.builder()
                .name(name)
                .parentId(parentId)
                .permissionOverwrites(set).build()).block();
        assert this.channel != null;
        return true;
    }

    protected boolean createLocalChannel(final String name, final Snowflake parentId) {
        return this.createLocalChannel(name, parentId, true);
    }

    protected Message send(final MessageCreateSpec spec, boolean block) {
        return send(this.channel, spec, block);
    }

    protected Message sendEmbed(final EmbedCreateSpec spec, boolean block) {
        return send(MessageCreateSpec.builder().addEmbed(spec).build(), block);
    }


    protected void deletedCommand(final long millis, final Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
            } finally {
                runnable.run();
                this.endCommand();
            }
        }).start();
    }

    protected void deletedCommand(final long millis) {
        this.deletedCommand(millis, () -> {
        });
    }

    public static Message send(final TextChannel channel, final MessageCreateSpec spec, boolean block) {
        try {
            try {
                if (block)
                    return channel.createMessage(spec).block();
                else {
                    channel.createMessage(spec).subscribe();
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Message is more than 2000 character !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Boolean delete(boolean block, final Message... messages) {
        boolean bool = true;
        for (Message message : messages)
            try {
                if (block)
                    message.delete().block();
                else
                    message.delete().subscribe(unused -> {
                    }, throwable -> {

                    });
            } catch (Exception e) {
                bool = false;
            }
        return bool;
    }

    public static Message sendEmbed(final TextChannel channel, final EmbedCreateSpec spec, boolean block) {
        return send(channel, MessageCreateSpec.builder().addEmbed(spec).build(), block);
    }

    public static Snowflake getMention(final MessageCreateEvent event) {
        return event.getMessage().getUserMentionIds().toArray(new Snowflake[0])[0];
    }

    public Member getMember() {
        return member;
    }


    /// DELETED !

    protected Message deletedMessage(final MessageCreateSpec spec) {
        return deletedMessage(this.channel, spec);
    }

    protected Message deletedEmbed(final EmbedCreateSpec spec) {
        return deletedMessage(MessageCreateSpec.builder().addEmbed(spec).build());
    }

    protected Message sendError(final String error) {
        return deletedEmbed(EmbedCreateSpec.builder()
                .title("Erreur !")
                .description(error)
                .color(ColorsUsed.wrong).build());
    }

    public static Message sendError(final TextChannel channel, final String error) {
        return deletedEmbed(channel, EmbedCreateSpec.builder()
                .title("Erreur !")
                .description(error)
                .color(ColorsUsed.wrong).build());
    }

    public static Message deletedMessage(final TextChannel channel, final MessageCreateSpec spec) {
        final Message atDelete = send(channel, spec, true);
        new Thread(() -> {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
            }
            delete(false, atDelete);
        }).start();
        return atDelete;
    }

    public static Message deletedEmbed(final TextChannel channel, final EmbedCreateSpec spec) {
        return deletedMessage(channel, MessageCreateSpec.builder().addEmbed(spec).build());
    }

    public void reply(InteractionApplicationCommandCallbackSpec reply, boolean block) {
        if (block) chatInteraction.reply(reply).block();
        else chatInteraction.reply(reply).subscribe();
    }

    public void reply(InteractionApplicationCommandCallbackSpec reply) {
        reply(reply, false);
    }

    public void replyEmbed(EmbedCreateSpec embed, boolean block) {
        reply(InteractionApplicationCommandCallbackSpec.builder().addEmbed(embed).build(), block);
    }

    public void replyEmbed(EmbedCreateSpec embed) {
        replyEmbed(embed, false);
    }

    public void replyError(String content) {
        reply(InteractionApplicationCommandCallbackSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .color(ColorsUsed.wrong)
                        .title("Erreur !")
                        .description(content)
                        .build())
                .ephemeral(true)
                .build());
    }

    public void endEditMessageForChatInteractionLongCommand(EmbedCreateSpec embed) {
        chatInteraction.editReply(InteractionReplyEditSpec.builder()
                .addEmbed(embed)
                .build()).subscribe();
    }
}
