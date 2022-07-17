package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.commands.SlashCommand;
import devarea.bot.presets.TextMessage;
import devarea.bot.commands.ShortCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;

public class Help extends ShortCommand implements SlashCommand {

    public Help() {
    }

    public Help(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        this.replyEmbed(TextMessage.helpEmbed);
        if (this.member.getBasePermissions().block().contains(Permission.ADMINISTRATOR) || this.member.getRoleIds().contains(Init.initial.admin_role) || this.member.getRoleIds().contains(Init.initial.modo_role)) {
            chatInteraction.createFollowup(InteractionFollowupCreateSpec.builder()
                    .ephemeral(true)
                    .addEmbed(TextMessage.helpEmbedAdmin)
                    .build()).subscribe();
        }
        this.endCommand();
    }


    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("help")
                .description("Donne une description de toutes les commandes du serveur.")
                .build();
    }
}
