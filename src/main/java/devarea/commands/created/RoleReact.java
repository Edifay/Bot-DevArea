package devarea.commands.created;

import devarea.Data.ColorsUsed;
import devarea.Main;
import devarea.automatical.MessageSeria;
import devarea.automatical.RolesReacts;
import devarea.commands.*;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Permission;

import java.util.function.Consumer;

public class RoleReact extends LongCommand {

    String emojiID;
    Snowflake roleID;
    Message atModif;
    String isID;

    public RoleReact(MessageCreateEvent message) {
        super(message);
        if (!message.getMember().get().getBasePermissions().block().contains(Permission.MANAGE_MESSAGES)) {
            sendError("Vous n'avez pas les permissions d'utiliser cette commande !");
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(CommandManager.actualCommands.containsValue(this));
                this.ended = true;
                this.endCommand();
            }).start();
        } else {

            Stape getRole = new EndStape() {
                @Override
                protected boolean onCall(Message message) {
                    setText(embed -> {
                        embed.setTitle("Le Role");
                        embed.setDescription("Mentionnez le role que vous voulez ajouter.");
                        embed.setColor(ColorsUsed.same);
                    });
                    return next;
                }

                @Override
                protected boolean onReceiveMessage(MessageCreateEvent event) {
                    try {
                        if (event.getMessage().getRoleMentionIds().stream().findFirst().isPresent()) {
                            roleID = event.getMessage().getRoleMentionIds().stream().findFirst().get();
                            devarea.commands.ObjetForStock.RoleReact react = new devarea.commands.ObjetForStock.RoleReact(emojiID, atModif, isID);
                            RolesReacts.rolesReacts.put(react, roleID);
                            RolesReacts.save();
                            atModif.addReaction(react.getEmoji()).subscribe();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return super.onReceiveMessage(event);
                }
            };

            Stape getEmoji = new Stape(getRole) {
                @Override
                protected boolean onCall(Message message) {
                    setText(embed -> {
                        embed.setTitle("Emoji");
                        embed.setDescription("Réagis a se message avec l'emoji que tu souhaite !");
                        embed.setColor(ColorsUsed.just);
                    });
                    return next;
                }

                @Override
                protected boolean onReceiveReact(ReactionAddEvent event) {
                    if (event.getEmoji().asCustomEmoji().isPresent()) {
                        emojiID = event.getEmoji().asCustomEmoji().get().getId().asString();
                        isID = "true";
                    } else {
                        emojiID = event.getEmoji().asUnicodeEmoji().get().getRaw();
                        isID = "false";
                    }

                    return callStape(0);
                }
            };


            this.firstStape = new FirstStape(this.channel, getEmoji) {
                @Override
                public void onFirstCall(Consumer<? super MessageCreateSpec> deleteThisVariableAndSetYourOwnMessage) {
                    super.onFirstCall(msg -> {
                        msg.setEmbed(embed -> {
                            embed.setTitle("Le message.");
                            embed.setDescription("Donnez-moi l'ID du message sur le quel vous voulez ajouter un roleReaction, ATTENTION vous devez vous trouver dans le channel du message !");
                            embed.setColor(ColorsUsed.same);
                        });
                    });
                }

                @Override
                protected boolean onReceiveMessage(MessageCreateEvent event) {
                    try {
                        atModif = this.textChannel.getMessageById(Snowflake.of(event.getMessage().getContent())).block();
                        if (atModif != null && atModif.getAuthor().get().getId().equals(Main.client.getSelfId())) {
                            System.out.println("Message du bot !");
                            return callStape(0);
                        }
                    } catch (Exception e) {
                    }
                    return super.onReceiveMessage(event);
                }
            };
            this.lastMessage = this.firstStape.getMessage();
        }
    }
}
