package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.automatical.RolesReactsHandler;
import devarea.bot.commands.*;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.Locale;

import static devarea.bot.automatical.RolesReactsHandler.getListByMessageRolesReact;

public class RoleReact extends LongCommand implements PermissionCommand, SlashCommand {

    String emojiID;
    Snowflake roleID;
    Message atModif;
    String isID;
    devarea.bot.commands.commandTools.RoleReact[] removeTable;

    public RoleReact(PermissionCommand permissionCommand) {
        super();
    }

    public RoleReact(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);

        Step getRole = new EndStep() {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder()
                        .title("Le Role")
                        .description("Mentionnez le rôle que vous voulez ajouter.")
                        .color(ColorsUsed.same).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                try {
                    if (event.getMessage().getRoleMentionIds().stream().findFirst().isPresent()) {
                        roleID = event.getMessage().getRoleMentionIds().stream().findFirst().get();
                        devarea.bot.commands.commandTools.RoleReact react =
                                new devarea.bot.commands.commandTools.RoleReact(emojiID, atModif, isID);
                        RolesReactsHandler.addNewRoleReact(react, roleID);
                        RolesReactsHandler.save();
                        atModif.addReaction(react.getEmoji()).subscribe();
                        endEditMessageForChatInteractionLongCommand(EmbedCreateSpec.builder()
                                .title("Création du RoleReact réussie !")
                                .color(ColorsUsed.just)
                                .description("Vous avez bien créé un lien entre " + (react.getStringEmoji()) + " -> " +
                                        "<@&" + roleID.asString() + "> !").build());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return super.onReceiveMessage(event);
            }
        };

        Step getEmoji = new Step(getRole) {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder()
                        .title("Emoji")
                        .description("Réagis à ce message avec l'émoji que tu souhaites !")
                        .color(ColorsUsed.just).build());
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


        Step firstStepCreate = new Step(getEmoji) {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder()
                        .title("Le message.")
                        .description("Donnez-moi l'ID du message sur lequel vous voulez ajouter un roleReaction, " +
                                "ATTENTION vous devez vous trouver dans le channel du message !")
                        .color(ColorsUsed.same).build());
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

        Step firstStepRemove = new EndStep() {
            @Override
            protected boolean onCall(Message message) {
                removeTable = new devarea.bot.commands.commandTools.RoleReact[RolesReactsHandler.getRoleReactCount()];
                String str = getListByMessageRolesReact(removeTable);
                str += "Donnez le numéro que vous souhaitez supprimer !";
                String finalStr = str;
                this.setText(EmbedCreateSpec.builder()
                        .title("Remove !")
                        .color(ColorsUsed.just)
                        .description(finalStr).build());
                return next;
            }


            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                try {
                    int number = Integer.parseInt(event.getMessage().getContent());
                    if (number >= 0 && number < removeTable.length) {
                        RolesReactsHandler.removeRoleReact(removeTable[number]);
                        RolesReactsHandler.save();
                        removeTable[number].delete();
                        endEditMessageForChatInteractionLongCommand(EmbedCreateSpec.builder()
                                .title("Remove effectué !")
                                .description("Vous avez bien supprimé le rolereact !")
                                .color(ColorsUsed.just).build());
                        return end;
                    }
                } catch (Exception e) {
                }
                sendErrorEntry();
                return next;
            }
        };


        this.firstStape = new FirstStep(this.channel, firstStepCreate, firstStepRemove) {
            @Override
            public void onFirstCall(MessageCreateSpec deleteThisVariableAndSetYourOwnMessage) {
                super.onFirstCall(MessageCreateSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Que voulez-vous faire ?")
                                .description("`create` -> créer un nouveau rolereact !\n`remove` -> supprimer tous " +
                                        "les rolereact !")
                                .color(ColorsUsed.same).build()

                        ).build());
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

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }

    public RoleReact() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("rolereact")
                .description("Commande admin pour ajouter ou supprimer des rôles réaction.")
                .build();
    }
}
