package devarea.bot.commands.inLine;

import devarea.Main;
import devarea.backend.controllers.rest.requestContent.RequestHandlerAuth;
import devarea.bot.commands.SlashCommand;
import devarea.global.cache.MemberCache;
import devarea.bot.commands.ShortCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.*;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.ArrayList;

import static devarea.bot.event.FunctionEvent.startAway;

public class Auth extends ShortCommand implements SlashCommand {

    public Auth(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        final Member reelMember =
                MemberCache.get(chatInteraction.getInteraction().getMember().get().getId().asString());

        final String code = RequestHandlerAuth.getCodeForMember(reelMember.getId().asString());
        chatInteraction.reply(InteractionApplicationCommandCallbackSpec.builder()
                .ephemeral(true)
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Authentification au site de Dev'area !")
                        .description("Vous venez de vous authentifier sur le site de dev'area !\n\nPour vous " +
                                "connecter utilisez ce lien :\n\n" + Main.domainName + "?code=" + code + "\n\nCe " +
                                "message sera supprimé d'ici **5 minutes** pour sécuriser l'accès. Si vous avez " +
                                "besoin de le retrouver exécutez de nouveau la commande !")
                        .color(ColorsUsed.just)
                        .build())
                .build()).subscribe();
        final ArrayList<EmbedCreateSpec> embeds = new ArrayList<>();
        embeds.add(EmbedCreateSpec.builder()
                .title("Authentification au site de Dev'area !")
                .description("Si vous voulez retrouver le lien d'authentification vous pouvez exécuter la commande " +
                        "`/auth` à nouveau !")
                .color(ColorsUsed.same)
                .build());
        startAway(() -> {
            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                chatInteraction.editReply(InteractionReplyEditSpec.builder()
                        .embeds(embeds)
                        .build()).subscribe();
            }
        });
        this.endCommand();
    }

    public Auth() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("auth")
                .description("Obtenez votre lien d'authentification au site de Dev'Area.")
                .build();
    }
}
