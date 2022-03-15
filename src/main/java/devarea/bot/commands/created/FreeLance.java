package devarea.bot.commands.created;

import devarea.bot.automatical.FreeLanceManager;
import devarea.bot.commands.Command;
import devarea.bot.commands.EndStape;
import devarea.bot.commands.FirstStape;
import devarea.bot.commands.LongCommand;
import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.util.function.Consumer;

public class FreeLance extends LongCommand {

    public FreeLance(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        EndStape bumpStape = new EndStape() {
            protected boolean onCall(Message message) {
                if (FreeLanceManager.hasFreelance(FreeLance.this.member)) {
                    if (FreeLanceManager.bumpFreeLance(FreeLance.this.member.getId().asString())) {
                        setText(EmbedCreateSpec.builder()
                                .title("Le bump a effectué !")
                                .color(ColorsUsed.just).build());
                    } else {
                        setText(EmbedCreateSpec.builder()
                                .title("Error")
                                .description("Vous devez attendre 24 heures entre chaque bump !")
                                .color(ColorsUsed.wrong).build());
                    }
                } else {
                    setText(EmbedCreateSpec.builder()
                            .title("Error")
                            .description("Vous ne possédez pas de freelance !")
                            .color(ColorsUsed.wrong).build());
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
                    setText(EmbedCreateSpec.builder()
                            .title("Suppression")
                            .description("Votre mission a bien été supprimé !")
                            .color(ColorsUsed.just).build());
                } else {
                    setText(EmbedCreateSpec.builder()
                            .title("Error")
                            .description("Vous n'avez pas de freelance !")
                            .color(ColorsUsed.wrong)
                            .build());
                }
                return true;
            }
        };
        this.firstStape = new FirstStape(this.channel, bumpStape, deleteStape) {
            @Override
            public void onFirstCall(MessageCreateSpec deleteThisVariableAndSetYourOwnMessage) {
                super.onFirstCall(MessageCreateSpec.builder().addEmbed(EmbedCreateSpec.builder()
                        .title("FreeLance")
                        .description("Vous pouvez ici effectuer des modifications sur votre freelance !\n`bump` -> cette commande va bump votre message freelance !\n" +
                                "`delete` -> supprimer votre freelance !")
                        .color(ColorsUsed.same)
                        .footer("cancel | annuler pour quitter.", null).build()).build());
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