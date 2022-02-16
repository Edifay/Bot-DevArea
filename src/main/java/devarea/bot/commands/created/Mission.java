package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.automatical.MissionsManager;
import devarea.bot.commands.FirstStape;
import devarea.bot.commands.LongCommand;
import devarea.bot.commands.Stape;
import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Mission extends LongCommand {
    ArrayList<devarea.bot.commands.object_for_stock.Mission> ofMember;

    public Mission(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);

        Stape deleteList = new Stape() {
            @Override
            protected boolean onCall(Message message) {
                ofMember = MissionsManager.getOf(member.getId());
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
                    setText(EmbedCreateSpec.builder()
                            .color(ColorsUsed.same)
                            .title("Vous n'avez acutellement acune mission !").build());
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
                        MissionsManager.remove(ofMember.get(number));
                        try {
                            ofMember.get(number).getMessage().getMessage().delete().subscribe();
                        } catch (Exception e) {
                        }
                        setText(EmbedCreateSpec.builder()
                                .color(ColorsUsed.just)
                                .title("Votre mission a bien été supprimé !").build());
                        return end;
                    }
                } catch (Exception e) {
                }
                return super.onReceiveMessage(event);
            }
        };

        this.firstStape = new FirstStape(this.channel, deleteList) {
            @Override
            public void onFirstCall(MessageCreateSpec deleteThisVariableAndSetYourOwnMessage) {
                super.onFirstCall(MessageCreateSpec.builder().addEmbed(EmbedCreateSpec.builder()
                        .color(ColorsUsed.same)
                        .title("Missions")
                        .description("`delete` -> supprimer une mission")
                        .addField("Créer une mission", "Pour créer une mission il suffit de réagir dans le channel <#" + Init.idMissionsPayantes.asString() + ">.", false)
                        .footer("Vous pouvez annuler | cancel", null).build()).build());
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (content.equalsIgnoreCase("delete")) {
                    return callStape(0);
                }
                return super.onReceiveMessage(event);
            }
        }

        ;
    }
}
