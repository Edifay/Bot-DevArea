package devarea.bot.automatical;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.MemberCache;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.MessageCreateSpec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EventMembers {

    public static String SAVED_FILE = "event.json";

    public static ArrayList<String> reactedMembers = new ArrayList<>();

    public static HashMap<String, String> colorNameToRoles = new HashMap<>();

    public static void init() {
        load();
        colorNameToRoles.put("rouge", "1011969861821411359");
        colorNameToRoles.put("rose", "1011970040364544000");
        colorNameToRoles.put("marron", "1011970050900631552");
        colorNameToRoles.put("noir", "1011970048115626105");
        colorNameToRoles.put("blanc", "1011970773415641208");
        colorNameToRoles.put("jaune", "1011970047276752896");
        colorNameToRoles.put("gris", "1011970770110533632");
        colorNameToRoles.put("bleu", "1011970042935644170");
        colorNameToRoles.put("vert", "1011970036858097756");
        colorNameToRoles.put("violet", "1011970045288656926");
        colorNameToRoles.put("orange", "1011970049596215386");
    }

    public static void load() {
        try {
            File file = new File(SAVED_FILE);
            if (!file.exists()) save();
            ObjectMapper mapper = new ObjectMapper();
            reactedMembers = mapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(SAVED_FILE), reactedMembers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean onReact(ButtonInteractionEvent event) {
        if (event.getCustomId().equals("2kevent")) {
            joinGiveAway(event);
            return true;
        }
        return false;
    }

    public static void joinGiveAway(ButtonInteractionEvent event) {
        if (event.getInteraction().getMember().isPresent() && reactedMembers.contains(event.getInteraction().getMember().get().getId().asString())) {
            event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .ephemeral(true)
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Vous êtes déjà sur la liste des participants !")
                            .color(ColorsUsed.same)
                            .build())
                    .build()).block();
        } else if (event.getInteraction().getMember().isPresent()) {
            addNewMember(event.getInteraction().getMember().get().getId().asString());
            System.out.println(event.getInteraction());
            event.reply(InteractionApplicationCommandCallbackSpec.builder()
                    .ephemeral(true)
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Vous avez été ajouté à la liste des participants !")
                            .color(ColorsUsed.same)
                            .build())
                    .build()).block();
        }
    }

    public static void addNewMember(String memberId) {
        reactedMembers.add(memberId);
        save();
    }

    public static void sendMessages() {
        ArrayList<SelectMenu.Option> options = new ArrayList<>();

        options.add(SelectMenu.Option.of("Rouge", "rouge"));
        options.add(SelectMenu.Option.of("Rose", "rose"));
        options.add(SelectMenu.Option.of("Marron", "marron"));
        options.add(SelectMenu.Option.of("Noir", "noir"));
        options.add(SelectMenu.Option.of("Blanc", "blanc"));
        options.add(SelectMenu.Option.of("Jaune", "jaune"));
        options.add(SelectMenu.Option.of("Gris", "gris"));
        options.add(SelectMenu.Option.of("Bleu", "bleu"));
        options.add(SelectMenu.Option.of("Vert", "vert"));
        options.add(SelectMenu.Option.of("Violet", "violet"));
        options.add(SelectMenu.Option.of("Orange", "orange"));


        for (String id : reactedMembers) {
            Member member = MemberCache.get(id);
            if (member != null) {
                try {
                    member.getPrivateChannel().block().createMessage(MessageCreateSpec.builder()
                            .components(ActionRow.of(SelectMenu.of("colorSelector", options)))
                            .addEmbed(EmbedCreateSpec.builder()
                                    .title("Tu as gagné ! :tada:")
                                    .description("Tu as été tiré au sort parmis tout les membres participants au " +
                                            "giveaway ! Voici ta récompense ! Tu peux choisir une couleur qui te sera" +
                                            " donné sur le serveur !")
                                    .color(ColorsUsed.same)

                                    .build())
                            .build()).block();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean onSelectColor(SelectMenuInteractionEvent event) {
        if (event.getCustomId().equals("colorSelector")) {
            System.out.println("Selection dans le menu !");
            System.out.println("Options number selected : " + event.getValues().size());
            System.out.println("Get the first option name : " + event.getValues().get(0));

            Member member = MemberCache.fetch(event.getInteraction().getUser().getId().asString());
            String selectedColor = event.getValues().get(0);

            if (member == null) {
                event.reply(InteractionApplicationCommandCallbackSpec.builder()
                        .ephemeral(true)
                        .content("Vous devez être sur le serveur pour réagir à ce message !")
                        .build()).subscribe();
                return true;
            }

            ArrayList<Snowflake> roles =
                    new ArrayList<>(Arrays.asList(member.getRoleIds().toArray(new Snowflake[0])));

            for (Map.Entry<String, String> bind : colorNameToRoles.entrySet()) {
                if (bind.getKey().equals(selectedColor)) {
                    if (!roles.contains(Snowflake.of(bind.getValue()))) {
                        member.addRole(Snowflake.of(bind.getValue())).subscribe();
                        event.reply(InteractionApplicationCommandCallbackSpec.builder()
                                .ephemeral(true)
                                .content("Vous venez de choisir la couleur " + selectedColor + ".")
                                .build()).subscribe();
                    } else {
                        event.reply(InteractionApplicationCommandCallbackSpec.builder()
                                .ephemeral(true)
                                .content("Vous avez déjà choisis la couleur " + selectedColor + ".")
                                .build()).subscribe();
                    }
                } else {
                    if (roles.contains(Snowflake.of(bind.getValue()))) {
                        member.removeRole(Snowflake.of(bind.getValue())).subscribe();
                    }
                }
            }
            if (!roles.contains(Snowflake.of("797882191337095198"))) {
                member.addRole(Snowflake.of("797882191337095198")).subscribe();
            }
            return true;
        }
        return false;
    }

}
