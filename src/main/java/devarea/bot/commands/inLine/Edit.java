package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.commands.*;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class Edit extends LongCommand implements PermissionCommand, SlashCommand {
    Message atModif = null;
    String title = "";
    String description = "";
    Color color = ColorsUsed.same;

    public Edit(PermissionCommand permissionCommand) {
        super();
    }

    public Edit() {
    }

    public Edit(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);

        Step endStep = new EndStep() {
            @Override
            protected boolean onCall(Message message) {
                endEditMessageForChatInteractionLongCommand(EmbedCreateSpec.builder()
                        .title("Votre message a été modifé !")
                        .color(ColorsUsed.just).build());
                return end;
            }
        };


        Step getMessageEnd = new Step(endStep) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Message !")
                                .description("Donnez moi le contenu du message a remplacé !")
                                .color(ColorsUsed.same).build()
                        )
                        .components(getEmptyButton())
                        .build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                String content = event.getMessage().getContent();
                if (!content.equals("")) {
                    atModif.edit(MessageEditSpec.builder().contentOrNull(content).build()).subscribe();
                    return callStape(0);
                }
                sendErrorEntry();
                return next;
            }
        };


        Step getEmbedColor = new Step(endStep) {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder()
                        .title("Couleur")
                        .description("Donnez moi la couleur que vous voulez, il y en a 3 disponible : `just`, `same`," +
                                " `wrong`")
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
                        atModif.edit(MessageEditSpec.builder()
                                .addEmbed(EmbedCreateSpec.builder()
                                        .title(title)
                                        .description(description)
                                        .color(color)
                                        .build())
                                .build()).subscribe();
                        return callStape(0);
                    }
                }
                return super.onReceiveMessage(event);
            }
        };

        Step getDescriptionTitle = new Step(getEmbedColor) {
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
                    atModif.edit(MessageEditSpec.builder().addEmbed(EmbedCreateSpec.builder()
                            .title(title)
                            .description(description)
                            .build()).build()).subscribe();
                    return callStape(0);
                }
                sendErrorEntry();
                return super.onReceiveMessage(event);
            }
        };

        Step getEmbedTitle = new Step(getDescriptionTitle) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Titre")
                                .description("Donnez moi le titre de votre embed")
                                .color(ColorsUsed.same).build())
                        .components(getEmptyButton())
                        .build());
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

        Step isEmbedOrText = new Step(getMessageEnd, getEmbedTitle) {
            @Override
            protected boolean onCall(Message message) {
                setMessage(MessageEditSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Type du message ?")
                                .description("Votre message est-il un embed ?")
                                .color(ColorsUsed.same).build()
                        )
                        .addComponent(getYesNoButton())
                        .build());
                return next;
            }

            @Override
            protected boolean onReceiveInteract(ButtonInteractionEvent event) {
                if (event.getCustomId().equals("yes")) {
                    return callStape(1);
                } else if (event.getCustomId().equals("no")) {
                    return callStape(0);
                }
                return super.onReceiveInteract(event);
            }

        };

        this.firstStape = new FirstStep(this.channel, isEmbedOrText) {
            @Override
            public void onFirstCall(MessageCreateSpec deleteThisVariableAndSetYourOwnMessage) {
                super.onFirstCall(MessageCreateSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Quel message voulez-vous modifier ?")
                                .description("Donnez-moi l'id du message a modifier, ATTENTION ce message doit être " +
                                        "dans le channel de cette commande !")
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
        };
        this.lastMessage = this.firstStape.getMessage();
    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.MANAGE_MESSAGES);
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("edit")
                .description("Permet d'éditer un message envoyé par le bot Dev'Area.")
                .build();
    }
}
