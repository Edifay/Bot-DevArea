package devarea.bot.commands.created;

import devarea.bot.data.ColorsUsed;
import devarea.bot.Init;
import devarea.bot.commands.*;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;

import java.util.function.Consumer;

public class Edit extends LongCommand {
    Message atModif = null;
    String title = "";
    String description = "";
    Color color = ColorsUsed.same;


    public Edit(MessageCreateEvent message) {
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
            Stape endStape = new EndStape() {
                @Override
                protected boolean onCall(Message message) {
                    setText(embedCreateSpec -> {
                        embedCreateSpec.setTitle("Votre message a été modifé !");
                        embedCreateSpec.setColor(ColorsUsed.just);
                    });
                    return end;
                }
            };


            Stape getMessageEnd = new Stape(endStape) {
                @Override
                protected boolean onCall(Message message) {
                    setMessage(msg -> {
                        msg.setEmbed(embed -> {
                            embed.setTitle("Message !");
                            embed.setDescription("Donnez moi le contenu du message a remplacé !");
                            embed.setColor(ColorsUsed.same);
                        });
                    });
                    return next;
                }

                @Override
                protected boolean onReceiveMessage(MessageCreateEvent event) {
                    String content = event.getMessage().getContent();
                    if (!content.equals("")) {
                        atModif.edit(messageEditSpec -> messageEditSpec.setContent(content)).subscribe();
                        return callStape(0);
                    }
                    sendErrorEntry();
                    return next;
                }
            };


            Stape getEmbedColor = new Stape(endStape) {
                @Override
                protected boolean onCall(Message message) {
                    setText(embed -> {
                        embed.setTitle("Couleur");
                        embed.setDescription("Donnez moi la couleur que vous voulez, il y en a 3 disponible : `just`, `same`, `wrong`");
                        embed.setColor(color);
                    });
                    return next;
                }

                @Override
                protected boolean onReceiveMessage(MessageCreateEvent event) {
                    String content = event.getMessage().getContent();
                    if (!content.equals("")) {
                        boolean find = false;
                        if (content.equalsIgnoreCase("same")) {
                            color = ColorsUsed.same;
                            find = true;
                        } else if (content.equalsIgnoreCase("wrong")) {
                            color = ColorsUsed.wrong;
                            find = true;
                        } else if (content.equalsIgnoreCase("just")) {
                            color = ColorsUsed.just;
                            find = true;
                        }
                        if (find) {
                            atModif.edit(messageEditSpec -> messageEditSpec.setEmbed(embed -> {
                                embed.setTitle(title);
                                embed.setDescription(description);
                                embed.setColor(color);
                            })).subscribe();
                            return callStape(0);
                        }
                    }
                    return super.onReceiveMessage(event);
                }
            };

            Stape getDescriptionTitle = new Stape(getEmbedColor) {
                @Override
                protected boolean onCall(Message message) {
                    setText(embed -> {
                        embed.setTitle("Description");
                        embed.setDescription("Donnez moi la description de votre embed");
                        embed.setColor(ColorsUsed.same);
                    });
                    return next;
                }

                @Override
                protected boolean onReceiveMessage(MessageCreateEvent event) {
                    String content = event.getMessage().getContent();
                    if (!content.equals("")) {
                        description = content;
                        atModif.edit(messageEditSpec -> messageEditSpec.setEmbed(embed -> {
                            embed.setTitle(title);
                            embed.setDescription(title);
                        }));
                        return callStape(0);
                    }
                    sendErrorEntry();
                    return super.onReceiveMessage(event);
                }
            };

            Stape getEmbedTitle = new Stape(getDescriptionTitle) {
                @Override
                protected boolean onCall(Message message) {
                    setText(embed -> {
                        embed.setTitle("Titre");
                        embed.setDescription("Donnez moi le titre de votre embed");
                        embed.setColor(ColorsUsed.same);
                    });
                    return next;
                }

                @Override
                protected boolean onReceiveMessage(MessageCreateEvent event) {
                    String content = event.getMessage().getContent();
                    if (!content.equals("")) {
                        title = content;
                        return callStape(0);
                    }
                    sendErrorEntry();
                    return super.onReceiveMessage(event);
                }
            };

            Stape isEmbedOrText = new Stape(getMessageEnd, getEmbedTitle) {
                @Override
                protected boolean onCall(Message message) {
                    setMessage(msg -> {
                        msg.setEmbed(embed -> {
                            embed.setTitle("Type du message ?");
                            embed.setDescription("Votre message est-il un embed ?");
                            embed.setColor(ColorsUsed.same);
                        });
                    });
                    addYesNoEmoji();
                    return next;
                }

                @Override
                protected boolean onReceiveReact(ReactionAddEvent event) {
                    if (isYes(event)) {
                        removeAllEmoji();
                        return callStape(1);
                    } else if (isNo(event)) {
                        removeAllEmoji();
                        return callStape(0);
                    }
                    return super.onReceiveReact(event);
                }
            };

            this.firstStape = new FirstStape(this.channel, isEmbedOrText) {
                @Override
                public void onFirstCall(Consumer<? super MessageCreateSpec> deleteThisVariableAndSetYourOwnMessage) {
                    super.onFirstCall(msg -> msg.setEmbed(embed -> {
                        embed.setTitle("Quel message voulez-vous modifier ?");
                        embed.setDescription("Donnez-moi l'id du message a modifier, ATTENTION ce message doit être dans le channel de cette commande !");
                        embed.setColor(ColorsUsed.same);
                    }));
                }

                @Override
                protected boolean onReceiveMessage(MessageCreateEvent event) {
                    try {
                        atModif = this.textChannel.getMessageById(Snowflake.of(event.getMessage().getContent())).block();
                        if (atModif != null && atModif.getAuthor().get().getId().equals(Init.client.getSelfId())) {
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
