package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.commands.*;
import devarea.bot.commands.with_out_text_starter.JoinThisCommand;
import devarea.bot.commands.with_out_text_starter.LeftThisCommand;
import devarea.bot.data.ColorsUsed;
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

    private static HashMap<String, ConsumableCommand> commands;

    public Admin(Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        member = Init.devarea.getMemberById(message.getAuthor().get().getId()).block();
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
