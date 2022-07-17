package devarea.bot.commands.inLine;

import devarea.bot.automatical.EmbedLinkHandler;
import devarea.bot.commands.SlashCommand;
import devarea.global.cache.MemberCache;
import devarea.bot.commands.ShortCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

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
        chatInteraction.reply("**Profile de : " + pinged.getUsername() + " :**").subscribe();
        EmbedLinkHandler.generateLinkEmbed(channel, new ArrayList<Member>(Collections.singleton(pinged)), true);
    }

    public Profile() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("profile")
                .description("Affiche votre profile web. Contient les badges, les levels...")
                .addOption(ApplicationCommandOptionData.builder()
                        .name("mention")
                        .description("Vous pouvez afficher le profile d'un membre du serveur.")
                        .required(false)
                        .type(ApplicationCommandOption.Type.MENTIONABLE.getValue())
                        .build())
                .build();
    }
}
