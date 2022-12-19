package devarea.bot.commands.inLine;

import devarea.bot.automatical.EventMembers;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.commands.SlashCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class SendGiveAwayMessage extends ShortCommand implements SlashCommand, PermissionCommand {
    public SendGiveAwayMessage() {
        super();
    }

    public SendGiveAwayMessage(final Member member, final ChatInputInteractionEvent event) {
        super(member, event);
        event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .ephemeral(true)
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Send GiveAway Message !")
                                .description("Vous venez d'envoyer un message au gagnant du giveaway !")
                                .color(ColorsUsed.same)
                                .build())
                .build()).subscribe();
        EventMembers.sendMessages();
    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("sendgiveawaymessage")
                .description("Envoyer un message au gagnant du give away !")
                .build();
    }
}
