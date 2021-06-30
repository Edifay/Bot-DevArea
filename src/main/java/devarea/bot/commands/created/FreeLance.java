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
                    if (FreeLanceManager.bumpFreeLance(FreeLance.this.member)) {
                        setText(embed -> embed.setTitle("Le bump a effectu!"));
                    } else {
                        setText(embed -> embed.setTitle("Vous devez attendre 24h entre chaque bump !"));
                    }
                } else {
                    setText(embed -> embed.setTitle("Vous ne posspas de freelance !"));
                }
                return true;
            }
        };
        EndStape deleteStape = new EndStape() {
            protected boolean onCall(Message message) {
                if (FreeLanceManager.hasFreelance(member)) {
                    devarea.bot.commands.object_for_stock.FreeLance free = FreeLanceManager.getFreeLance(member);
                    FreeLanceManager.remove(free);
                    try {
                        Command.delete(false, free.getMessage().getMessage());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    setText(embed -> embed.setTitle("Votre mission a bien supprimer !"));
                } else {
                    setText(embed -> embed.setTitle("Vous ne posspas de freelance !"));
                }
                return true;
            }
        };
        this.firstStape = new FirstStape(this.channel, bumpStape, deleteStape) {
            public void onFirstCall(Consumer<? super MessageCreateSpec> deleteThisVariableAndSetYourOwnMessage) {
                super.onFirstCall(msg -> msg.setEmbed(embed -> {
                    embed.setTitle("FreeLance");
                    embed.setColor(ColorsUsed.just);
                    embed.setDescription("Vous pouvez ici effectué des modifications sur votre freelance !\n`bump` -> cette commande va bump votre message freelance !\n" +
                            "`delete` -> supprimer votre freelance !");
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