package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.commands.SlashCommand;
import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.ChannelCache;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.EmojiData;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class SetupEventMembers extends ShortCommand implements SlashCommand, PermissionCommand {

    public SetupEventMembers(PermissionCommand permissionCommand) {
        super();
    }

    public SetupEventMembers() {
        super();
    }

    public SetupEventMembers(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        chatInteraction.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Vous venez d'envoyer le message de mise en place de l'event pour les membres.")
                        .color(ColorsUsed.same)
                        .build())
                .build()).subscribe();

        send(MessageCreateSpec.builder()
                .content("@everyone")
                .addEmbed(EmbedCreateSpec.builder()
                        .title("2000 membres :partying_face: !")
                        .description("Nous allons faire un petit giveaway pour l'occasion ! \n\nLa récompense est " +
                                "pour l'instant inconnue, le gagnant recevra un message privé du bot ! (Suprise du chef !)\n\nCliquez sur :tada: pour participer !\n\nGG à tous ! Et merci pour tout " +
                                "!\nEdifay ;) !")
                        .author(EmbedCreateFields.Author.of("Edifay", null, "https://cdn.discordapp" +
                                ".com/avatars/321673326105985025/20c7dd75e6a11e00a768bce8ad7ab1ef.webp?size=96"))
                        .color(ColorsUsed.same)
                        .build())
                .components(ActionRow.of(Button.primary("2kevent", ReactionEmoji.unicode("\uD83C\uDF89"))))
                .build(), false);

    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("setupeventmembers")
                .description("Permet d'envoyer le message pour l'event.")
                .build();
    }
}
