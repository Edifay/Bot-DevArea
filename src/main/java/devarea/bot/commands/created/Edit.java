package devarea.bot.commands.created;

import devarea.bot.Init;
import devarea.bot.commands.*;
import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.function.Consumer;

public class Edit extends LongCommand implements PermissionCommand {
    Message atModif = null;
    String title = "";
    String description = "";
    Color color = ColorsUsed.same;

    public Edit(PermissionCommand permissionCommand) {
        super();
    }


    public Edit(MessageCreateEvent message) {
        super(message);

        Stape endStape = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder()
                        .title("Votre message a été modifé !")
                        .color(ColorsUsed.just).build()
                );
                return end;
            }
        };


        Stape getMessageEnd = new Stape(endStape) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Message !")
                                .description("Donnez moi le contenu du message a remplacé !")
                                .color(ColorsUsed.same).build()
                        ).build());
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
                setText(EmbedCreateSpec.builder()
                        .title("Couleur")
                        .description("Donnez moi la couleur que vous voulez, il y en a 3 disponible : `just`, `same`, `wrong`")
                        .color(color).build());
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
                setText(EmbedCreateSpec.builder()
                        .title("Description")
                        .description("Donnez moi la description de votre embed")
                        .color(ColorsUsed.same).build());
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
                setText(EmbedCreateSpec.builder()
                        .title("Titre")
                        .description("Donnez moi le titre de votre embed")
                        .color(ColorsUsed.same).build());
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
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Type du message ?")
                                .description("Votre message est-il un embed ?")
                                .color(ColorsUsed.same).build()
                        ).build());
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

        this.firstStape = new

                FirstStape(this.channel, isEmbedOrText) {
                    @Override
                    public void onFirstCall(MessageCreateSpec deleteThisVariableAndSetYourOwnMessage) {
                        super.onFirstCall(MessageCreateSpec.builder()
                                .addEmbed(EmbedCreateSpec.builder()
                                        .title("Quel message voulez-vous modifier ?")
                                        .description("Donnez-moi l'id du message a modifier, ATTENTION ce message doit être dans le channel de cette commande !")
                                        .color(ColorsUsed.same).build()
                                ).build());
                    }

                    @Override
                    protected boolean onReceiveMessage(MessageCreateEvent event) {
                        try {
                            atModif = this.textChannel.getMessageById(Snowflake.of(event.getMessage().getContent())).block();
                            if (atModif != null && atModif.getAuthor().get().getId().equals(Init.client.getSelfId()))
                                return callStape(0);
                        } catch (Exception e) {
                        }
                        return super.onReceiveMessage(event);
                    }
                }

        ;
        this.lastMessage = this.firstStape.getMessage();
    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.MANAGE_MESSAGES);
    }
}
