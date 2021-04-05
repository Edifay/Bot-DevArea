package devarea.automatical;

import devarea.Data.ColorsUsed;
import devarea.Main;
import devarea.commands.Command;
import devarea.commands.CommandManager;
import devarea.commands.withOutTextStarter.CreateMission;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;

public class MissionsManager {

    public static Message messsage;

    public static void init() {
        messsage = Command.sendEmbed((TextChannel) Main.devarea.getChannelById(Main.idMissionsPayantes).block(), embedCreateSpec -> {
            embedCreateSpec.setColor(ColorsUsed.same);
            embedCreateSpec.setTitle("Créer une mission.");
            embedCreateSpec.setDescription("Cliquez sur <:ayy:" + Main.idYes.asString() + "> pour créer une mission !");
        });
        messsage.addReaction(ReactionEmoji.custom(Main.devarea.getGuildEmojiById(Main.idYes).block())).block();
    }

    public static void update() {
        Command.delete(messsage);
        messsage = Command.sendEmbed((TextChannel) Main.devarea.getChannelById(Main.idMissionsPayantes).block(), embedCreateSpec -> {
            embedCreateSpec.setColor(ColorsUsed.same);
            embedCreateSpec.setTitle("Créer une mission.");
            embedCreateSpec.setDescription("Cliquez sur <:ayy:" + Main.idYes.asString() + "> pour créer une mission !");
        });
        messsage.addReaction(ReactionEmoji.custom(Main.devarea.getGuildEmojiById(Main.idYes).block())).block();
    }

    public static void react(ReactionAddEvent event) {
        System.out.println("react");
        if (event.getMessageId().equals(messsage.getId()) && event.getEmoji().asCustomEmoji().get().getId().equals(Main.idYes) && !CommandManager.actualCommands.containsKey(event.getMember().get().getId()))
            CommandManager.actualCommands.put(event.getMember().get().getId(), new CreateMission(event));

        if (event.getMessageId().equals(messsage.getId()))
            event.getMessage().block().removeReaction(event.getEmoji(), event.getUserId()).block();

    }

    public static void stop() {
        Command.delete(messsage);
    }

}