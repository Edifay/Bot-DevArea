package devarea.bot.commands.created;

import devarea.bot.automatical.Stats;
import devarea.bot.commands.PermissionCommand;
import devarea.bot.commands.ShortCommand;
import devarea.bot.data.ColorsUsed;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class Update extends ShortCommand implements PermissionCommand {

    public Update(PermissionCommand permissionCommand) {
        super();
    }

    public Update(final Member member, final TextChannel channel, final Message message) {
        super(member, channel);
        final long ms = System.currentTimeMillis();
        Stats.update();
        sendEmbed(EmbedCreateSpec.builder()
                .title("Update !")
                .description("Les stats ont été update en " + (System.currentTimeMillis() - ms) + "ms.")
                .color(ColorsUsed.just).build(), false);
        this.endCommand();
    }


    @Override
    public PermissionSet getPermissions() {
        return PermissionSet.of(Permission.ADMINISTRATOR);
    }
}
