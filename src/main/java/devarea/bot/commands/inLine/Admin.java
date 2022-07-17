package devarea.bot.commands.inLine;

import devarea.global.cache.MemberCache;
import devarea.bot.commands.*;
import devarea.bot.commands.outLine.JoinThisCommand;
import devarea.bot.commands.outLine.LeftThisCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommand;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Admin extends ShortCommand implements SlashCommand {

    static {
        commands = new HashMap<>();
        setupBindedCommands();
    }

    private static final HashMap<String, ConsumableCommand> commands;

    public Admin(Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        member = MemberCache.get(chatInteraction.getInteraction().getMember().get().getId().asString());
        if (chatInteraction.getOption("sous-commande").isEmpty() || chatInteraction.getOption("sous-commande").get().getValue().isEmpty()) {
            EmbedCreateSpec.Builder builer =
                    EmbedCreateSpec.builder().title("Voici les commandes disponibles :").color(ColorsUsed.same);
            AtomicReference<String> allCommands = new AtomicReference<>("");
            commands.forEach((str, com) -> allCommands.set(allCommands.get() + "- `" + str + "`\n"));
            builer.description(allCommands.get());
            reply(InteractionApplicationCommandCallbackSpec.builder().ephemeral(true).addEmbed(builer.build()).build());
        } else {
            String firstArg = chatInteraction.getOption("sous-commande").get().getValue().get().asString();
            Member finalMember = member;
            commands.forEach((str, com) -> {
                if (firstArg.equalsIgnoreCase(str)) {
                    com.setChannel(channel);
                    com.setMember(finalMember);
                    com.setChatInteraction(chatInteraction);
                    CommandManager.addManualCommand(finalMember, com, true);
                }
            });
        }
    }

    public static void setupBindedCommands() {
        commands.put("join", new ConsumableCommand(JoinThisCommand.class) {
            @Override
            protected Command command() {
                return new JoinThisCommand(this.member, this.chatInteraction);
            }
        });
        commands.put("left", new ConsumableCommand(LeftThisCommand.class) {
            @Override
            protected Command command() {
                return new LeftThisCommand(this.member, this.chatInteraction);
            }
        });
    }

    public MessageCreateSpec getFirstMessage() {
        EmbedCreateSpec.Builder builer =
                EmbedCreateSpec.builder().title("Voici les commandes disponibles :").color(ColorsUsed.same);
        AtomicReference<String> allCommands = new AtomicReference<>("");
        commands.forEach((str, com) -> allCommands.set(allCommands.get() + "- `" + str + "`\n"));
        builer.description(allCommands.get());
        return MessageCreateSpec.builder().addEmbed(builer.build()).build();
    }

    public Admin() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("admin")
                .description("Commande dédier aux administrateur !")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("sous-commande")
                        .description("Dites nous quelle commande administrateur voulez vous utiliser.")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(false).build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name("mention").description("Si vous utilisez une commande admin necessitant une mention.")
                        .type(ApplicationCommandOption.Type.MENTIONABLE.getValue())
                        .required(false).build())
                .build();
    }
}
