package devarea.bot.commands;

import devarea.bot.Init;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static devarea.bot.data.TextMessage.haventPermission;

public abstract class Command {

    protected final Member member;
    protected TextChannel channel;

    public Command() {
        this.member = null;
    }

    public Command(final MessageCreateEvent event) {
        this(event.getMember().get(), (TextChannel) event.getMessage().getChannel().block());
    }

    public Command(final ReactionAddEvent event) {
        this(event.getMember().get(), (TextChannel) event.getChannel().block());
    }

    public Command(final Member member) {
        this.member = member;
    }

    public Command(final Member member, final TextChannel channel) {
        this.member = member;
        this.channel = channel;
    }


    protected Boolean endCommand() {
        CommandManager.removeCommand(this.member.getId(), this);
        return true;
    }

    protected Message deletedMessage(final Consumer<? super MessageCreateSpec> spec) {
        return deletedMessage(this.channel, spec);
    }

    protected Message deletedEmbed(final Consumer<? super EmbedCreateSpec> spec) {
        return deletedMessage(msg -> msg.setEmbed(spec));
    }

    protected boolean createLocalChannel(final String name, final Snowflake parentId) {
        this.channel = Init.devarea.createTextChannel(textChannelCreateSpec -> {
            textChannelCreateSpec.setName(name);
            textChannelCreateSpec.setParentId(parentId);
            Set<PermissionOverwrite> set = new HashSet<>();
            set.add(PermissionOverwrite.forRole(Init.idRoleRulesAccepted,
                    PermissionSet.of(),
                    PermissionSet.of(Permission.VIEW_CHANNEL)));
            set.add(PermissionOverwrite.forMember(this.member.getId(),
                    PermissionSet.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY, Permission.SEND_MESSAGES),
                    PermissionSet.of(Permission.ADD_REACTIONS)));
            textChannelCreateSpec.setPermissionOverwrites(set);
        }).block();
        assert this.channel != null;
        return true;
    }

    protected Message send(final Consumer<? super MessageCreateSpec> spec, boolean block) {
        return send(this.channel, spec, block);
    }

    protected Message sendEmbed(final Consumer<? super EmbedCreateSpec> spec, boolean block) {
        return send(msg -> msg.setEmbed(spec), block);
    }

    protected Message sendError(final String error) {
        return deletedEmbed(embed -> {
            embed.setTitle("Error !");
            embed.setDescription(error);
            embed.setColor(ColorsUsed.wrong);
        });
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

    public static Message send(final TextChannel channel, final Consumer<? super MessageCreateSpec> spec, boolean block) {
        try {
            try {
                if (block)
                    return channel.createMessage(spec).block();
                else {
                    channel.createMessage(spec).subscribe();
                    return null;
                }
            } catch (Exception ignored) {
                throw new Exception("Message is more than 2000 character !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Message sendError(final TextChannel channel, final String error) {
        return deletedEmbed(channel, embed -> {
            embed.setTitle("Error !");
            embed.setDescription(error);
            embed.setColor(ColorsUsed.wrong);
        });
    }

    public static Message deletedMessage(final TextChannel channel, final Consumer<? super MessageCreateSpec> spec) {
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

    public static Message deletedEmbed(final TextChannel channel, final Consumer<? super EmbedCreateSpec> spec) {
        return deletedMessage(channel, msg -> msg.setEmbed(spec));
    }

    public static Message sendEmbed(final TextChannel channel, final Consumer<? super EmbedCreateSpec> spec, boolean block) {
        return send(channel, msg -> msg.setEmbed(spec), block);
    }


}
