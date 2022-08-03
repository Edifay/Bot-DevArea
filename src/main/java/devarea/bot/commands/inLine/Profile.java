package devarea.bot.commands.inLine;

import devarea.Main;
import devarea.bot.automatical.EmbedLinkHandler;
import devarea.bot.commands.SlashCommand;
import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.MemberCache;
import devarea.bot.commands.ShortCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Profile extends ShortCommand implements SlashCommand {

    public Profile(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        Member pinged = member;
        if (chatInteraction.getOption("mention").isPresent() && chatInteraction.getOption("mention").get().getValue().isPresent()) {
            pinged =
                    MemberCache.get(chatInteraction.getOption("mention").get().getValue().get().asSnowflake().asString());
            if (pinged == null)
                pinged = member;
        }
        try {
            ByteArrayInputStream image_stream = EmbedLinkHandler.generateImageStreamForMember(pinged);
            chatInteraction.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Profil de : " + member.getDisplayName())
                            .image("attachment://profil.png")
                            .color(ColorsUsed.same)
                            .build())
                    .addFile("profil.png", image_stream)
                    .addComponent(ActionRow.of(Button.link(Main.domainName + "member-profile?member_id=" + member.getId().asString(), "devarea.fr")))
                    .build()).subscribe();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Profile() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("profile")
                .description("Affiche votre profil web. Contient les badges, les levels...")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("mention")
                        .description("Vous pouvez afficher le profil d'un membre du serveur.")
                        .required(false)
                        .type(ApplicationCommandOption.Type.MENTIONABLE.getValue())
                        .build())
                .build();
    }
}
