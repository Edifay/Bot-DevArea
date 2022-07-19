package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.commands.ShortCommand;
import devarea.bot.commands.SlashCommand;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.ArrayList;

import static devarea.global.utils.ThreadHandler.startAwayIn;

public class DevHelp extends ShortCommand implements SlashCommand {

    private static final ArrayList<Snowflake> timer = new ArrayList<>();

    public DevHelp(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        if (channel.getName().equalsIgnoreCase("entraide")) {
            if (!timer.contains(this.channel.getId())) {
                reply(InteractionApplicationCommandCallbackSpec.builder().content("<@" + this.member.getId().asString() + ">, a demandé de " +
                        "l'aide ! <@&" + Init.initial.devHelper_role.asString() + ">.").build(), false);
                timer.add(this.channel.getId());
                startAwayIn(() -> timer.remove(channel.getId()), 1800000, false);
            } else
                replyError("La commande devhelp n'est disponible que toutes les 30 minutes.");
        } else
            replyError("Uniquement les channels d'entraide acceptent cette commande.");
        this.endCommand();
    }

    public DevHelp() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("devhelp")
                .description("Commande qui permet d'envoyer un ping au développeurs volontaires dans les channels entraides.")
                .build();
    }
}
