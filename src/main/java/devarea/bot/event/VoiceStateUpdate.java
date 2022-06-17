package devarea.bot.event;

import devarea.bot.Init;
import devarea.bot.automatical.VoiceChannelHandler;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.TextChannelEditSpec;
import discord4j.core.spec.VoiceChannelEditSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class VoiceStateUpdate {

    public static void VoiceStateUpdateFucntion(VoiceStateUpdateEvent event) {
        if (event.getCurrent().getUser().block().isBot())
            return;

        if (event.getCurrent().getChannelId().isPresent()) {
            if (event.getOld().isEmpty() || event.getOld().get().getChannelId().isEmpty())
                ((TextChannel) Init.devarea.getChannelById(Init.initial.noMic_channel).block()).edit(TextChannelEditSpec.builder()
                        .addPermissionOverwrite(PermissionOverwrite.forMember(event.getCurrent().getUserId(),
                                PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of()))
                        .build()).subscribe();
            VoiceChannelHandler.join(event);
        } else {
            ((TextChannel) Init.devarea.getChannelById(Init.initial.noMic_channel).block()).edit(TextChannelEditSpec.builder()
                    .addPermissionOverwrite(PermissionOverwrite.forMember(event.getCurrent().getUserId(),
                            PermissionSet.of(),
                            PermissionSet.of(Permission.VIEW_CHANNEL)))
                    .build()).subscribe();
            VoiceChannelHandler.leave(event);
        }
    }

}
