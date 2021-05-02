package devarea.commands;

import devarea.Data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Permission;

import java.util.Arrays;
import java.util.function.Consumer;

import static devarea.Data.TextMessage.haventPermission;

public abstract class Command {

    protected final MessageCreateEvent message;
    protected final ReactionAddEvent reaction;
    protected final Member member;
    protected TextChannel channel;

    protected Boolean ended = false;
    protected String[] args;

    public Command(final MessageCreateEvent message) {
        this.message = message;
        this.reaction = null;
        this.member = this.message.getMember().get();
        this.channel = (TextChannel) this.message.getMessage().getChannel().block();
        final String[] alls = this.message.getMessage().getContent().split(" ");
        this.args = Arrays.copyOfRange(alls, 1, alls.length);
    }

    public Command(final ReactionAddEvent message) {
        this.reaction = message;
        this.message = null;
        this.member = this.reaction.getMember().get();
        this.channel = (TextChannel) this.reaction.getMessage().block().getChannel().block();
    }


    protected Boolean endCommand() {
        CommandManager.actualCommands.remove(message.getMember().get().getId());
        return ended;
    }

    protected Message deletedMessage(final Consumer<? super MessageCreateSpec> spec) {
        return deletedMessage(this.channel, spec);
    }

    protected Message deletedEmbed(final Consumer<? super EmbedCreateSpec> spec) {
        return deletedMessage(msg -> msg.setEmbed(spec));
    }

    protected Boolean commandWithPerm(final Permission permission, final Runnable runnable) {
        if (this.message.getMember().get().getBasePermissions().block().contains(permission) || this.message.getMember().get().getBasePermissions().block().contains(Permission.ADMINISTRATOR)) {
            runnable.run();
            return true;
        } else
            deletedEmbed(embedCreateSpec -> {
                embedCreateSpec.setTitle("Erreur !");
                embedCreateSpec.setDescription(haventPermission);
                embedCreateSpec.setColor(ColorsUsed.wrong);
            });
        return false;
    }

    protected Message send(final Consumer<? super MessageCreateSpec> spec) {
        return send(this.channel, spec);
    }

    protected Message sendEmbed(final Consumer<? super EmbedCreateSpec> spec) {
        return send(msg -> msg.setEmbed(spec));
    }

    protected Message sendError(final String error) {
        return deletedEmbed(embed -> {
            embed.setTitle("Error !");
            embed.setDescription(error);
            embed.setColor(ColorsUsed.wrong);
        });
    }

    protected void deletedCommand() {
        deletedCommand(600000L);
    }

    protected void deletedCommand(long millis) {
        new Thread(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (CommandManager.actualCommands.containsValue(this))
                    this.endCommand();
            }
        }).start();
    }

    public static Message send(final TextChannel channel, final Consumer<? super MessageCreateSpec> spec) {
        return channel.createMessage(spec).block();
    }

    public static Message sendError(final TextChannel channel, final String error) {
        return deletedEmbed(channel, embed -> {
            embed.setTitle("Error !");
            embed.setDescription(error);
            embed.setColor(ColorsUsed.wrong);
        });
    }

    public static Message deletedMessage(final TextChannel channel, final Consumer<? super MessageCreateSpec> spec) {
        final Message atDelete = send(channel, spec);
        new Thread(() -> {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            delete(atDelete);
        }).start();
        return atDelete;
    }

    public static Boolean delete(final Message... messages) {
        boolean bool = true;
        for (Message message : messages)
            try {
                message.delete().block();
            } catch (Exception e) {
                bool = false;
            }
        return bool;
    }

    public static Message deletedEmbed(final TextChannel channel, final Consumer<? super EmbedCreateSpec> spec) {
        return deletedMessage(channel, msg -> msg.setEmbed(spec));
    }

    public static Message sendEmbed(final TextChannel channel, final Consumer<? super EmbedCreateSpec> spec) {
        return send(channel, msg -> msg.setEmbed(spec));
    }


}
