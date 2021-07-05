package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.commands.Command;
import devarea.bot.commands.CommandManager;
import devarea.bot.commands.ConsumableCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.commands.with_out_text_starter.JoinCommand;
import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;

public class Join extends ShortCommand {

    public Join(MessageCreateEvent message) {
        super(message);
        if (message.getMessage().getAuthor().get().getId().asString().equals("321673326105985025")) {
            if (message.getMessage().getUserMentions().buffer().count().block() > 0) {
                Member member = message.getMessage().getUserMentions().blockFirst().asMember(Init.devarea.getId()).block();
                assert member != null;
                CommandManager.addManualCommand(member.getId(), new ConsumableCommand() {
                    @Override
                    protected Command command() {
                        return new JoinCommand(member);
                    }
                });
                sendEmbed(embed -> {
                    embed.setTitle("Vous avez fait join " + member.getDisplayName() + " !");
                    embed.setColor(ColorsUsed.just);
                }, false);
            }
        } else {
            sendError("Vous n'avez pas la permission d'utiliser cette commande !");
        }
    }
}
