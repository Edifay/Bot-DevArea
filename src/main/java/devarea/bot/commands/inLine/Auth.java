package devarea.bot.commands.inLine;

import devarea.Main;
import devarea.backend.controllers.rest.requestContent.RequestHandlerAuth;
import devarea.bot.cache.MemberCache;
import devarea.bot.commands.ShortCommand;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;

import java.util.ArrayList;

import static devarea.bot.event.FunctionEvent.startAway;

public class Auth extends ShortCommand {

    public Auth(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        final Member reelMember = MemberCache.get(message.getAuthor().get().getId().asString());

        final String code = RequestHandlerAuth.getCodeForMember(reelMember.getId().asString());
        sendEmbed(EmbedCreateSpec.builder()
                .title("Authentification réussie !")
                .description("Toutes les informations liées à l'authentification au site de **Dev'Area** t'ont été " +
                        "transmises par Message Privé !\n\nSi tu ne l'as pas reçu n'hésite pas à nous contacter !")
                .color(ColorsUsed.just)
                .build(), false);
        final Message message_at_edit = reelMember.getPrivateChannel().block().createMessage(MessageCreateSpec.builder()
                .addEmbed(EmbedCreateSpec.builder()
                        .title("Authentification au site de Dev'area !")
                        .description("Vous venez de vous authentifier sur le site de dev'area !\n\nPour vous " +
                                "connecter utilisez ce lien :\n\n" + Main.domainName + "?code=" + code + "\n\nCe " +
                                "message sera supprimé d'ici **5 minutes** pour sécuriser l'accès. Si vous avez " +
                                "besoin de le retrouver exécutez de nouveau la commande !")
                        .color(ColorsUsed.just)
                        .build())
                .build()).block();
        final ArrayList<EmbedCreateSpec> embeds = new ArrayList<>();
        embeds.add(EmbedCreateSpec.builder()
                .title("Authentification au site de Dev'area !")
                .description("Si vous voulez retrouver le lien d'authentification vous pouvez exécuter la commande " +
                        "`//auth` à nouveau !")
                .color(ColorsUsed.same)
                .build());
        startAway(() -> {
            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                message_at_edit.edit(MessageEditSpec.builder()
                        .embeds(embeds)
                        .build()).subscribe();
            }
        });
        this.endCommand();
    }

}