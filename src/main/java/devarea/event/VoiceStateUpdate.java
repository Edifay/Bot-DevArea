package devarea.event;

import devarea.Main;
import devarea.automatical.HelpVoiceChannel;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class VoiceStateUpdate {

    public static void VoiceStateUpdateFucntion(VoiceStateUpdateEvent event) {
        if (event.getCurrent().getChannelId().isPresent()) {
            Main.devarea.getChannelById(Main.idNoMic).block().addMemberOverwrite(event.getCurrent().getUserId(), PermissionOverwrite.forMember(event.getCurrent().getUserId(), PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of())).block();
            HelpVoiceChannel.join(event);
        } else {
            Main.devarea.getChannelById(Main.idNoMic).block().addMemberOverwrite(event.getCurrent().getUserId(), PermissionOverwrite.forMember(event.getCurrent().getUserId(), PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL))).block();
            HelpVoiceChannel.leave(event);
        }
    }

}
