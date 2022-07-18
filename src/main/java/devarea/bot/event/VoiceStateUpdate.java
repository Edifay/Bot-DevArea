package devarea.bot.event;

import devarea.bot.Init;
import devarea.bot.automatical.VoiceChannelHandler;
import devarea.global.cache.ChannelCache;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.TextChannelEditSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class VoiceStateUpdate {

    public static void VoiceStateUpdateFunction(VoiceStateUpdateEvent event) {
        if (event.getCurrent().getUser().block().isBot())
            return;

        if (event.getCurrent().getChannelId().isPresent()) {
            if (event.getOld().isEmpty() || event.getOld().get().getChannelId().isEmpty()) { // make no-mic visible
                ((TextChannel) ChannelCache.watch(Init.initial.noMic_channel.asString())).addMemberOverwrite(event.getCurrent().getUserId(), PermissionOverwrite.forMember(event.getCurrent().getUserId(),
                        PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of())).subscribe();
            }
            VoiceChannelHandler.join(event);
        } else {
            // make no-mic invisible
            ((TextChannel) ChannelCache.watch(Init.initial.noMic_channel.asString())).addMemberOverwrite(event.getCurrent().getUserId(), PermissionOverwrite.forMember(event.getCurrent().getUserId(),
                    PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL))).subscribe();
            VoiceChannelHandler.leave(event);
        }
    }

}
