package devarea.commands.created;

import devarea.Main;
import devarea.automatical.MissionsManager;
import devarea.commands.FirstStape;
import devarea.commands.LongCommand;
import devarea.commands.Stape;
import devarea.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Mission extends LongCommand {
    ArrayList<devarea.commands.object_for_stock.Mission> ofMember;

    public Mission(MessageCreateEvent message) {
        super(message);

        Stape deleteList = new Stape() {
            @Override
            protected boolean onCall(Message message) {
                ofMember = MissionsManager.getOf(member.getId());
                if (ofMember.size() != 0)
                    setText(embed -> {
                        embed.setColor(ColorsUsed.same);
                        embed.setTitle("Vous possédez : " + ofMember.size());
                        String msg = "";
                        for (int i = 0; i < ofMember.size(); i++)
                            msg += "`" + i + "`: **" + ofMember.get(i).getTitle() + "**\n";
                        embed.setDescription(msg);
                        embed.setFooter("Vous pouvez annuler | cancel", null);
                    });
                else {
                    setText(embed -> {
                        embed.setColor(ColorsUsed.same);
                        embed.setTitle("Vous n'avez acutellement acune mission !");
                    });
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
                        setText(embed -> {
                            embed.setColor(ColorsUsed.just);
                            embed.setTitle("Votre mission a bien été supprimé !");
                        });
                        return end;
                    }
                } catch (Exception e) {
                }
                return super.onReceiveMessage(event);
            }
        };

        this.firstStape = new FirstStape(this.channel, deleteList) {
            @Override
            public void onFirstCall(Consumer<? super MessageCreateSpec> deleteThisVariableAndSetYourOwnMessage) {
                super.onFirstCall(msg -> {
                    msg.setEmbed(embed -> {
                        embed.setColor(ColorsUsed.same);
                        embed.setTitle("Missions");
                        embed.setDescription("`delete` -> supprimer une mission");
                        embed.addField("Créer une mission", "Pour créer une mission il suffit de réagir dans le channel <#" + Main.idMissionsPayantes.asString() + ">.", false);
                        embed.setFooter("Vous pouvez annuler | cancel", null);
                    });
                });
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (content.equalsIgnoreCase("delete")) {
                    return callStape(0);
                }
                return super.onReceiveMessage(event);
            }
        };
    }
}
