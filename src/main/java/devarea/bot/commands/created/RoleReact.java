package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.automatical.RolesReacts;
import devarea.bot.commands.EndStape;
import devarea.bot.commands.FirstStape;
import devarea.bot.commands.LongCommand;
import devarea.bot.commands.Stape;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Permission;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class RoleReact extends LongCommand {

    String emojiID;
    Snowflake roleID;
    Message atModif;
    String isID;
    devarea.bot.commands.object_for_stock.RoleReact[] removeTable;

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
                            devarea.bot.commands.object_for_stock.RoleReact react = new devarea.bot.commands.object_for_stock.RoleReact(emojiID, atModif, isID);
                            RolesReacts.rolesReacts.put(react, roleID);
                            RolesReacts.save();
                            atModif.addReaction(react.getEmoji()).subscribe();
                            this.setText(embed -> {
                                embed.setTitle("Création du RoleReact réussi !");
                                embed.setColor(ColorsUsed.just);
                                embed.setDescription("Vous avez bien créé un lien entre " + (react.getStringEmoji()) + " -> <@&" + roleID.asString() + "> !");
                            });
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


            Stape firstStapeCreate = new Stape(getEmoji) {
                @Override
                protected boolean onCall(Message message) {
                    setText(embed -> {
                        embed.setTitle("Le message.");
                        embed.setDescription("Donnez-moi l'ID du message sur le quel vous voulez ajouter un roleReaction, ATTENTION vous devez vous trouver dans le channel du message !");
                        embed.setColor(ColorsUsed.same);
                    });
                    return next;
                }

                @Override
                protected boolean onReceiveMessage(MessageCreateEvent event) {
                    try {
                        atModif = channel.getMessageById(Snowflake.of(event.getMessage().getContent())).block();
                        if (atModif != null && atModif.getAuthor().get().getId().equals(Init.client.getSelfId())) {
                            return callStape(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return super.onReceiveMessage(event);
                }
            };

            Stape firstStapeRemove = new EndStape() {
                @Override
                protected boolean onCall(Message message) {
                    String str = "";
                    ArrayList<Snowflake> messageAlready = new ArrayList<>();
                    int number = 0;
                    removeTable = new devarea.bot.commands.object_for_stock.RoleReact[RolesReacts.rolesReacts.size()];
                    for (Map.Entry<devarea.bot.commands.object_for_stock.RoleReact, Snowflake> entry : RolesReacts.rolesReacts.entrySet()) {
                        devarea.bot.commands.object_for_stock.RoleReact k = entry.getKey();
                        Snowflake v = entry.getValue();
                        Snowflake snow = k.getMessageSeria().getMessageID();
                        boolean find = false;
                        for (Snowflake s : messageAlready)
                            if (s.equals(snow)) {
                                find = true;
                                break;
                            }

                        if (!find) {
                            str += "https://discord.com/channels/" + Init.devarea.getId().asString() + "/" + k.getMessageSeria().getChannelID().asString() + "/" + snow.asString() + " :\n";
                            for (Map.Entry<devarea.bot.commands.object_for_stock.RoleReact, Snowflake> e : RolesReacts.rolesReacts.entrySet()) {
                                devarea.bot.commands.object_for_stock.RoleReact k1 = e.getKey();
                                Snowflake v1 = e.getValue();
                                if (k1.getMessageSeria().getMessageID().equals(snow)) {
                                    str += "`" + number + "`:" + k1.getStringEmoji() + " -> <@&" + v1.asString() + ">\n";
                                    removeTable[number] = k1;
                                    number++;
                                }
                            }
                            messageAlready.add(snow);
                        }
                    }
                    str += "Donnez le numéro que vous souhaitez supprimer !";
                    String finalStr = str;
                    this.setText(embed -> {
                        embed.setTitle("Remove !");
                        embed.setColor(ColorsUsed.just);
                        embed.setDescription(finalStr);
                    });
                    return next;
                }

                @Override
                protected boolean onReceiveMessage(MessageCreateEvent event) {
                    try {
                        int number = Integer.parseInt(event.getMessage().getContent());
                        if (number >= 0 && number < removeTable.length) {
                            RolesReacts.rolesReacts.remove(removeTable[number]);
                            RolesReacts.save();
                            removeTable[number].delete();
                            setText(embed -> {
                                embed.setTitle("Remove effectué !");
                                embed.setDescription("Vous avez bien supprimer le rolereact !");
                                embed.setColor(ColorsUsed.just);
                            });
                            return end;
                        }
                    } catch (Exception e) {
                    }
                    sendErrorEntry();
                    return next;
                }
            };


            this.firstStape = new FirstStape(this.channel, firstStapeCreate, firstStapeRemove) {
                @Override
                public void onFirstCall(Consumer<? super MessageCreateSpec> deleteThisVariableAndSetYourOwnMessage) {
                    super.onFirstCall(msg -> {
                        msg.setEmbed(embed -> {
                            embed.setTitle("Que voulez-vous faire ?");
                            embed.setDescription("`create` -> créer un nouveau rolereact !\n`remove` -> supprimer tout les rolereact !");
                            embed.setColor(ColorsUsed.same);
                        });
                    });
                }

                @Override
                protected boolean onReceiveMessage(MessageCreateEvent event) {
                    String content = event.getMessage().getContent().toLowerCase(Locale.ROOT);
                    if (content.equals("create")) {
                        return callStape(0);
                    } else if (content.equals("remove")) {
                        return callStape(1);
                    }
                    sendErrorEntry();
                    return next;
                }
            };
            this.lastMessage = this.firstStape.getMessage();
        }
    }
}
