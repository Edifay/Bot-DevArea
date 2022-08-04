package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.commands.*;
import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.RoleCache;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.SelectOptionData;
import discord4j.discordjson.possible.Possible;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RoleMenu extends LongCommand implements SlashCommand, PermissionCommand {

    public static final String SELECTOR = "switchRole:";

    public Message atModif;
    public List<Snowflake> roleIDS;

    public RoleMenu() {

    }

    public RoleMenu(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);

        Step getRole = new EndStep() {
            @Override
            protected boolean onCall(Message message) {
                setText(EmbedCreateSpec.builder()
                        .title("Le Role")
                        .description("Mentionnez le(s) rôle(s) que vous voulez ajouter.")
                        .color(ColorsUsed.same).build());
                return next;
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                try {
                    if (event.getMessage().getRoleMentionIds().stream().findFirst().isPresent()) {
                        roleIDS =  event.getMessage().getRoleMentionIds();

                        endEditMessageForChatInteractionLongCommand(EmbedCreateSpec.builder()
                                .title("Création du RoleMenu réussie !")
                                .color(ColorsUsed.just)
                                .description("Vous avez bien créé un lien entre le(s) rôle(s) et le " +
                                        "menu du message !").build());

                        SelectMenu menu;

                        List<SelectMenu.Option> options = new ArrayList<>();
                        System.out.println("AtModif components number : " + atModif.getComponents().size());
                        if (atModif.getComponents().size() != 0)
                            for (SelectOptionData option :
                                    atModif.getComponents().get(0).getChildren().get(0).getData().options().get()) {
                                options.add(SelectMenu.Option.of(option.label(), option.value()));
                            }

                        for (Snowflake role : roleIDS)
                            options.add(SelectMenu.Option.of(RoleCache.get(role.asString()).getName(),
                                    "switchRole:" + role.asString()));

                        menu = SelectMenu.of("roleMenu", options).withMinValues(0).withMaxValues(options.size());

                        atModif.edit(MessageEditSpec.builder()
                                .components(Possible.of(Optional.of(List.of(ActionRow.of(menu)))))
                                .build()).subscribe();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return super.onReceiveMessage(event);
            }
        };

        Step selectCreateStep = new Step(getRole) {
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
                        return callStep(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return super.onReceiveMessage(event);
            }
        };

        this.firstStep = new FirstStep(this.channel, selectCreateStep) {
            @Override
            public void onFirstCall(MessageCreateSpec deleteThisVariableAndSetYourOwnMessage) {
                super.onFirstCall(MessageCreateSpec.builder()
                        .addEmbed(EmbedCreateSpec.builder()
                                .title("RoleMenu")
                                .description("Vous pouvez utiliser : \n\n - `create` : pour créer un roleMenu !")
                                .color(ColorsUsed.same)
                                .build())
                        .build());
            }

            @Override
            protected boolean onReceiveMessage(MessageCreateEvent event) {
                if (event.getMessage().getContent().equalsIgnoreCase("create")) {
                    System.out.println("Call Step !");
                    return callStep(0);
                }
                return super.onReceiveMessage(event);
            }
        };
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("rolemenu")
                .description("Gérer les RoleMenu.")
                .build();
    }

    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }
}
