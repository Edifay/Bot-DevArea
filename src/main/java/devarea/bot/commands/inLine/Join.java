package devarea.bot.commands.inLine;

import devarea.bot.commands.*;
import devarea.bot.commands.outLine.JoinCommand;
import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.MemberCache;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class Join extends ShortCommand implements PermissionCommand, SlashCommand {

    public Join(PermissionCommand permissionCommand) {
        super();
    }

    public Join(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        if (chatInteraction.getOption("mention").isEmpty() || chatInteraction.getOption("mention").get().getValue().isEmpty()) {
            replyError("Vous devez rentrer une mention valide !");
            return;
        }
        Member memberPinged =
                MemberCache.get(chatInteraction.getOption("mention").get().getValue().get().asSnowflake().asString());
        if (memberPinged == null) {
            replyError("Vous devez rentrer une mention valide !");
            return;
        }
        CommandManager.addManualCommand(memberPinged, new ConsumableCommand(JoinCommand.class) {
            @Override
            protected Command command() {
                return new JoinCommand(this.member);
            }
        });
        replyEmbed(EmbedCreateSpec.builder()
                .title("Vous avez fait join " + memberPinged.getDisplayName() + " !")
                .color(ColorsUsed.just).build(), false);
    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }

    public Join() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("join")
                .description("Commande qui permet de faire join un membre. Il refait donc le questionnaire.")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("mention")
                        .description("Mention de la personne à faire join.")
                        .required(true)
                        .type(ApplicationCommandOption.Type.MENTIONABLE.getValue())
                        .build())
                .build();
    }
}
