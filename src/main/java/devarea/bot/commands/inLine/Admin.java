package devarea.bot.commands.inLine;

import devarea.global.cache.MemberCache;
import devarea.bot.commands.*;
import devarea.bot.commands.outLine.JoinThisCommand;
import devarea.bot.commands.outLine.LeftThisCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Admin extends ShortCommand {

    static {
        commands = new HashMap<>();
        setupBindedCommands();
    }

    private static final HashMap<String, ConsumableCommand> commands;

    public Admin(Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        member = MemberCache.get(message.getAuthor().get().getId().asString());
        if (message.getContent().split(" ").length == 1) {
            EmbedCreateSpec.Builder builer = EmbedCreateSpec.builder()
                    .title("Voici les commandes disponibles :")
                    .color(ColorsUsed.same);
            AtomicReference<String> allCommands = new AtomicReference<>("");
            commands.forEach((str, com) -> allCommands.set(allCommands.get() + "- `" + str + "`\n"));
            builer.description(allCommands.get());
            Command.sendEmbed(this.channel, builer.build(), false);
        } else {
            String firstArg = message.getContent().split(" ")[1];
            Member finalMember = member;
            commands.forEach((str, com) -> {
                if (firstArg.equalsIgnoreCase(str)) {
                    com.setMessageEvent(message, finalMember);
                    CommandManager.addManualCommand(finalMember, com, true);
                }
            });
        }
    }

    public static void setupBindedCommands() {
        commands.put("join", new ConsumableCommand(JoinThisCommand.class) {
            @Override
            protected Command command() {
                return new JoinThisCommand(this.member, this.channel, this.message);
            }
        });
        commands.put("left", new ConsumableCommand(LeftThisCommand.class) {
            @Override
            protected Command command() {
                return new LeftThisCommand(this.member, this.channel, this.message);
            }
        });
    }

    public MessageCreateSpec getFirstMessage() {
        EmbedCreateSpec.Builder builer = EmbedCreateSpec.builder()
                .title("Voici les commandes disponibles :")
                .color(ColorsUsed.same);
        AtomicReference<String> allCommands = new AtomicReference<>("");
        commands.forEach((str, com) -> allCommands.set(allCommands.get() + "- `" + str + "`\n"));
        builer.description(allCommands.get());
        return MessageCreateSpec.builder().addEmbed(builer.build()).build();
    }
}
