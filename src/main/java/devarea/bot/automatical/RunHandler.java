package devarea.bot.automatical;

import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.ConsumableCommand;
import devarea.bot.commands.inLine.Run;
import devarea.global.cache.ChannelCache;
import devarea.global.cache.MemberCache;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.rest.util.AllowedMentions;

import java.util.LinkedHashMap;
import java.util.Map;

public class RunHandler {

    final static int MAX_MESSAGES = 400;

    private static final Map<Snowflake, Snowflake> latestMessages = new LinkedHashMap<>();

    public static boolean onEdit(Message messageHandler) {
        if (!latestMessages.containsKey(messageHandler.getId())
                || !messageHandler.getContent().startsWith(Init.initial.prefix + "run")
                || messageHandler.getAuthor().isEmpty()) {
            return false;
        }

        TextChannel channelHander = (TextChannel) ChannelCache.get(messageHandler.getChannelId().asString());

        if (channelHander == null) {
            return false;
        }

        CommandManager.addManualCommand(MemberCache.get(messageHandler.getAuthor().get().getId().asString()),
                new ConsumableCommand(Run.class) {
            @Override
            protected Command command() {
                return new Run(MemberCache.get(messageHandler.getAuthor().get().getId().asString()), channelHander,
                        messageHandler);
            }
        });

        return true;
    }

    public static boolean onDelete(Message message) {
        Snowflake replyId = latestMessages.remove(message.getId());

        if (replyId == null) {
            return false;
        }

        TextChannel channel = (TextChannel) ChannelCache.get(message.getChannelId().asString());
        Command.delete(false, channel.getMessageById(replyId).block());
        return true;
    }

    public static void addMessage(Snowflake message, Snowflake reply) {
        latestMessages.put(message, reply);
        if (latestMessages.size() > MAX_MESSAGES) {
            latestMessages.remove(latestMessages.keySet().iterator().next());
        }
    }

    public static void sendResponse(Message message, EmbedCreateSpec spec, boolean edit) {
        Snowflake replyId = latestMessages.get(message.getId());
        TextChannel channel = (TextChannel) ChannelCache.get(message.getChannelId().asString());
        Message reply;

        if (channel == null) {
            return;
        }

        if (edit && replyId != null && (reply = channel.getMessageById(replyId).block()) != null) {
            reply.edit(MessageEditSpec.builder().addEmbed(spec).build()).subscribe();
        } else if (replyId == null) {
            reply = channel.createMessage(MessageCreateSpec.builder()
                    .addEmbed(spec)
                    .messageReference(message.getId())
                    .allowedMentions(AllowedMentions.suppressAll())
                    .build()).block();
            if (edit && reply != null) {
                addMessage(message.getId(), reply.getId());
            }
        }
    }
}
