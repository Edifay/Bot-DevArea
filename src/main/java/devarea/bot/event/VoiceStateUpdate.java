package devarea.bot.event;

import devarea.bot.Init;
import devarea.bot.automatical.HelpVoiceChannel;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class VoiceStateUpdate {

    public static void VoiceStateUpdateFucntion(VoiceStateUpdateEvent event) {
        if (event.getCurrent().getChannelId().isPresent()) {
            if (event.getOld().isEmpty() || event.getOld().get().getChannelId().isEmpty())
                Init.devarea.getChannelById(Init.idNoMic).block().addMemberOverwrite(event.getCurrent().getUserId(), PermissionOverwrite.forMember(event.getCurrent().getUserId(), PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of())).subscribe();
            HelpVoiceChannel.join(event);
        } else {
            Init.devarea.getChannelById(Init.idNoMic).block().addMemberOverwrite(event.getCurrent().getUserId(), PermissionOverwrite.forMember(event.getCurrent().getUserId(), PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL))).subscribe();
            HelpVoiceChannel.leave(event);
        }
    }

}
