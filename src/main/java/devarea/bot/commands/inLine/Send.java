package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.commands.SlashCommand;
import devarea.bot.presets.TextMessage;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class Send extends ShortCommand implements PermissionCommand, SlashCommand {

    public Send(PermissionCommand permissionCommand) {
        super();
    }

    public Send(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        final String strMessage = chatInteraction.getOption("message").get().getValue().get().asString();
        if (!strMessage.isEmpty())
            send(MessageCreateSpec.builder().content(strMessage).build(), false);
        else
            sendError(TextMessage.errorNeedArguments);
        chatInteraction.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .content("Votre message a bien été envoyé !")
                .build()).subscribe();
        this.endCommand();
    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.MANAGE_MESSAGES);
    }

    public Send() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("send")
                .description("Commande admin, permet de faire envoyer un message au bot.")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("message")
                        .description("Le message à faire envoyer par le bot.")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(true)
                        .build())
                .build();
    }
}
