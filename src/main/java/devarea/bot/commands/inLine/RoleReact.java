package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.automatical.RolesReactsHandler;
import devarea.bot.commands.*;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class RoleReact extends LongCommand implements PermissionCommand {

    String emojiID;
    Snowflake roleID;
    Message atModif;
    String isID;
    devarea.bot.commands.commandTools.RoleReact[] removeTable;

    public RoleReact(PermissionCommand permissionCommand) {
        super();
    }

    public RoleReact(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);


        Stape getRole = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder()
                        .title("Le Role")
                        .description("Mentionnez le role que vous voulez ajouter.")
                        .color(ColorsUsed.same).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                try {
                    if (event.getMessage().getRoleMentionIds().stream().findFirst().isPresent()) {
                        roleID = event.getMessage().getRoleMentionIds().stream().findFirst().get();
                        devarea.bot.commands.commandTools.RoleReact react = new devarea.bot.commands.commandTools.RoleReact(emojiID, atModif, isID);
                        RolesReactsHandler.rolesReacts.put(react, roleID);
                        RolesReactsHandler.save();
                        atModif.addReaction(react.getEmoji()).subscribe();
                        this.setText(EmbedCreateSpec.builder()
                                .title("Création du RoleReact réussi !")
                                .color(ColorsUsed.just)
                                .description("Vous avez bien créé un lien entre " + (react.getStringEmoji()) + " -> <@&" + roleID.asString() + "> !").build());
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
                setText(EmbedCreateSpec.builder()
                        .title("Emoji")
                        .description("Réagis a se message avec l'emoji que tu souhaite !")
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


        Stape firstStapeCreate = new Stape(getEmoji) {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder()
                        .title("Le message.")
                        .description("Donnez-moi l'ID du message sur le quel vous voulez ajouter un roleReaction, ATTENTION vous devez vous trouver dans le channel du message !")
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

        Stape firstStapeRemove = new EndStape() {
            @Override
            protected boolean onCall(Message message) {
                String str = "";
                ArrayList<Snowflake> messageAlready = new ArrayList<>();
                int number = 0;
                removeTable = new devarea.bot.commands.commandTools.RoleReact[RolesReactsHandler.rolesReacts.size()];
                for (Map.Entry<devarea.bot.commands.commandTools.RoleReact, Snowflake> entry : RolesReactsHandler.rolesReacts.entrySet()) {
                    devarea.bot.commands.commandTools.RoleReact k = entry.getKey();
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
                        for (Map.Entry<devarea.bot.commands.commandTools.RoleReact, Snowflake> e : RolesReactsHandler.rolesReacts.entrySet()) {
                            devarea.bot.commands.commandTools.RoleReact k1 = e.getKey();
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
                        RolesReactsHandler.rolesReacts.remove(removeTable[number]);
                        RolesReactsHandler.save();
                        removeTable[number].delete();
                        setText(EmbedCreateSpec.builder()
                                .title("Remove effectué !")
                                .description("Vous avez bien supprimer le rolereact !")
                                .color(ColorsUsed.just).build());
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
            public void onFirstCall(MessageCreateSpec deleteThisVariableAndSetYourOwnMessage) {
                super.onFirstCall(MessageCreateSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("Que voulez-vous faire ?")
                                .description("`create` -> créer un nouveau rolereact !\n`remove` -> supprimer tout les rolereact !")
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
}
