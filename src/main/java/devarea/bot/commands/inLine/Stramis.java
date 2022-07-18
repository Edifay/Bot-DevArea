package devarea.bot.commands.inLine;

import devarea.bot.commands.ShortCommand;
import devarea.bot.commands.SlashCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Stramis extends ShortCommand implements SlashCommand {

    public Stramis() {

    }

    public Stramis(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        chatInteraction.reply("https://tenor.com/view/happy-tgif-drinking-drunk-dance-gif-5427975").subscribe();
        this.endCommand();
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("stramis")
                .description("Les fautes de frappe de Stramis lui ont coûté cher ^^ !")
                .build();
    }
}
