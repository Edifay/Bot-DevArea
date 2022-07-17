package devarea.bot.commands.inLine;

import devarea.bot.commands.*;
import devarea.global.handlers.FreeLanceHandler;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class FreeLance extends LongCommand implements SlashCommand {
    public FreeLance() {
    }

    public FreeLance(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        chatInteraction.deferReply().subscribe();

        EndStape bumpStape = new EndStape() {
            protected boolean onCall(Message message) {
                if (FreeLanceHandler.hasFreelance(FreeLance.this.member)) {
                    if (FreeLanceHandler.bumpFreeLance(FreeLance.this.member.getId().asString()))
                        editEmbed(EmbedCreateSpec.builder()
                                .title("Le bump a effectué !")
                                .color(ColorsUsed.just).build());
                    else
                        editEmbed(EmbedCreateSpec.builder()
                                .title("Error")
                                .description("Vous devez attendre 24 heures entre chaque bump !")
                                .color(ColorsUsed.wrong).build());

                } else
                    editEmbed(EmbedCreateSpec.builder()
                            .title("Error")
                            .description("Vous ne possédez pas de freelance !")
                            .color(ColorsUsed.wrong).build());
                delete(false, this.message);
                return true;
            }
        };

        EndStape deleteStape = new EndStape() {
            protected boolean onCall(Message message) {
                if (FreeLanceHandler.hasFreelance(member)) {
                    devarea.bot.commands.commandTools.FreeLance free = FreeLanceHandler.getFreelance(member);
                    FreeLanceHandler.remove(free);
                    try {
                        Command.delete(false, free.getMessage().getMessage());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    editEmbed(EmbedCreateSpec.builder()
                            .title("Suppression")
                            .description("Votre freelance a bien été supprimé !")
                            .color(ColorsUsed.just).build());
                } else {
                    editEmbed(EmbedCreateSpec.builder()
                            .title("Error")
                            .description("Vous n'avez pas de freelance !")
                            .color(ColorsUsed.wrong)
                            .build());
                }
                delete(false, this.message);
                return true;
            }
        };
        this.firstStape = new FirstStape(this.channel, bumpStape, deleteStape) {
            @Override
            public void onFirstCall(MessageCreateSpec deleteThisVariableAndSetYourOwnMessage) {
                super.onFirstCall(MessageCreateSpec.builder().addEmbed(EmbedCreateSpec.builder()
                        .title("FreeLance")
                        .description("Vous pouvez ici effectuer des modifications sur votre freelance !\n`bump` -> " +
                                "cette commande va bump votre message freelance !\n" +
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

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("freelance")
                .description("Permet de gérer votre page freelance.")
                .build();
    }
}