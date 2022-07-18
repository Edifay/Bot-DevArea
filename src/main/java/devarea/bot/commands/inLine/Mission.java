package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.commands.*;
import devarea.global.handlers.MissionsHandler;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.ArrayList;

public class Mission extends LongCommand implements SlashCommand {
    ArrayList<devarea.bot.commands.commandTools.Mission> ofMember;

    public Mission(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);

        Step deleteList = new EndStep() {
            @Override
            protected boolean onCall(Message message) {
                ofMember = MissionsHandler.getOf(member.getId());
                if (ofMember.size() != 0) {
                    String msg = "";
                    for (int i = 0; i < ofMember.size(); i++)
                        msg += "`" + i + "`: **" + ofMember.get(i).getTitle() + "**\n";
                    setText(EmbedCreateSpec.builder()
                            .color(ColorsUsed.same)
                            .title("Vous possédez : " + ofMember.size())
                            .description(msg)
                            .footer("Vous pouvez annuler | cancel", null).build());
                } else {
                    endEditMessageForChatInteractionLongCommand(EmbedCreateSpec.builder()
                            .color(ColorsUsed.same)
                            .title("Vous n'avez actuellement aucune mission !").build());
                    return end;
                }
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                try {
                    Integer number = Integer.parseInt(content);
                    if (number >= 0 && number < ofMember.size()) {
                        MissionsHandler.clearThisMission(ofMember.get(number));
                        endEditMessageForChatInteractionLongCommand(EmbedCreateSpec.builder()
                                .color(ColorsUsed.just)
                                .title("Votre mission a bien été supprimée !").build());
                        return end;
                    }
                } catch (Exception e) {
                }
                return next;
            }
        };

        this.firstStep = new FirstStep(this.channel, deleteList) {
            @Override
            public void onFirstCall(MessageCreateSpec deleteThisVariableAndSetYourOwnMessage) {
                super.onFirstCall(MessageCreateSpec.builder().addEmbed(EmbedCreateSpec.builder()
                        .color(ColorsUsed.same)
                        .title("Missions")
                        .description("`delete` -> supprimer une mission")
                        .addField("Créer une mission", "Pour créer une mission il suffit de réagir dans le channel " +
                                "<#" + Init.initial.paidMissions_channel.asString() + ">.", false)
                        .addField("Le site", "Vous avez la possibilité de contrôler vos missions à partir du site " +
                                "internet, dans l'onglet options : https://devarea.fr.", false)
                        .footer("Vous pouvez annuler | cancel", null).build()).build());
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (content.equalsIgnoreCase("delete")) {
                    return callStep(0);
                }
                return super.onReceiveMessage(event);
            }
        };
    }

    public Mission() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("mission")
                .description("Permet de contrôler et gérer les missions possédées !")
                .build();
    }
}
