package devarea.bot.commands.created;

import devarea.bot.automatical.FreeLanceManager;
import devarea.bot.commands.Command;
import devarea.bot.commands.EndStape;
import devarea.bot.commands.FirstStape;
import devarea.bot.commands.LongCommand;
import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;

import java.util.function.Consumer;

public class FreeLance extends LongCommand {

    public FreeLance(MessageCreateEvent message) {
        super(message);
        EndStape bumpStape = new EndStape() {
            protected boolean onCall(Message message) {
                if (FreeLanceManager.hasFreelance(FreeLance.this.member)) {
                    if (FreeLanceManager.bumpFreeLance(FreeLance.this.member.getId().asString())) {
                        setText(embed -> {
                            embed.setTitle("Bump !");
                            embed.setTitle("Le bump a effectué !");
                            embed.setColor(ColorsUsed.just);
                        });
                    } else {
                        setText(embed -> {
                            embed.setTitle("Error");
                            embed.setDescription("Vous devez attendre 24 heures entre chaque bump !");
                            embed.setColor(ColorsUsed.wrong);
                        });
                    }
                } else {
                    setText(embed -> {
                        embed.setTitle("Error");
                        embed.setDescription("Vous ne possédez pas de freelance !");
                        embed.setColor(ColorsUsed.wrong);
                    });
                }
                return true;
            }
        };

        EndStape deleteStape = new EndStape() {
            protected boolean onCall(Message message) {
                if (FreeLanceManager.hasFreelance(member)) {
                    devarea.bot.commands.object_for_stock.FreeLance free = FreeLanceManager.getFreelance(member);
                    FreeLanceManager.remove(free);
                    try {
                        Command.delete(false, free.getMessage().getMessage());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    setText(embed -> {
                        embed.setTitle("Suppression");
                        embed.setDescription("Votre mission a bien été supprimé !");
                        embed.setColor(ColorsUsed.just);
                    });
                } else {
                    setText(embed -> {
                        embed.setTitle("Error");
                        embed.setDescription("Vous n'avez pas de freelance !");
                        embed.setColor(ColorsUsed.wrong);
                    });
                }
                return true;
            }
        };
        this.firstStape = new FirstStape(this.channel, bumpStape, deleteStape) {
            public void onFirstCall(Consumer<? super MessageCreateSpec> deleteThisVariableAndSetYourOwnMessage) {
                super.onFirstCall(msg -> msg.setEmbed(embed -> {
                    embed.setTitle("FreeLance");
                    embed.setColor(ColorsUsed.same);
                    embed.setDescription("Vous pouvez ici effectuer des modifications sur votre freelance !\n`bump` -> cette commande va bump votre message freelance !\n" +
                            "`delete` -> supprimer votre freelance !");
                    embed.setFooter("cancel | annuler pour quitter.", null);
                }));
            }

            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = getContent(event);
                if (content.equalsIgnoreCase("bump"))
                    return callStape(0);
                if (content.equalsIgnoreCase("delete"))
                    return callStape(1);
                return super.onReceiveMessage(event);
            }
        };
        this.lastMessage = this.firstStape.getMessage();
    }
}