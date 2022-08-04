package devarea.bot.automatical;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.discordjson.json.SelectOptionData;

import static devarea.bot.commands.inLine.RoleMenu.SELECTOR;

public class RoleMenuHandler {

    public static void onReceiveMenuSelect(SelectMenuInteractionEvent event) {
        if (event.getCustomId().equals("roleMenu")) {
            System.out.println("values : " + event.getValues());

            StringBuilder addedRoles = new StringBuilder();
            StringBuilder removeRoles = new StringBuilder();

            for (SelectOptionData options :
                    event.getMessage().get().getComponents().get(0).getChildren().get(0).getData().options().get()) {
                String id = options.value().substring(SELECTOR.length());
                if (event.getValues().contains(options.value())) {
                    if (event.getInteraction().getMember().get().getRoleIds().contains(Snowflake.of(id)))
                        continue;
                    addedRoles.append("<@&" + id + ">");
                    event.getInteraction().getMember().get().addRole(Snowflake.of(id)).subscribe();
                } else if (event.getInteraction().getMember().get().getRoleIds().contains(Snowflake.of(id))) {
                    removeRoles.append("<@&" + id + ">");
                    event.getInteraction().getMember().get().removeRole(Snowflake.of(id)).subscribe();
                }
            }

            if (addedRoles.isEmpty())
                addedRoles.append("...");
            if (removeRoles.isEmpty())
                removeRoles.append("...");


            event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .ephemeral(true)
                    .content("Vous avez ajouté " + addedRoles + " vous avez enlevé " + removeRoles + " !")
                    .build()).subscribe();
        }
    }

}
