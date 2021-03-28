package devarea.event;

import devarea.Main;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.PermissionOverwrite;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class VoiceStateUpdate {

    public static void VoiceStateUpdateFucntion(VoiceStateUpdateEvent event) {
        if (event.getCurrent().getChannelId().isPresent())
            Main.devarea.getChannelById(Main.idNoMic).block().addMemberOverwrite(event.getCurrent().getUserId(), PermissionOverwrite.forMember(event.getCurrent().getUserId(), PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.of())).block();
        else
            Main.devarea.getChannelById(Main.idNoMic).block().addMemberOverwrite(event.getCurrent().getUserId(), PermissionOverwrite.forMember(event.getCurrent().getUserId(), PermissionSet.of(), PermissionSet.of(Permission.VIEW_CHANNEL))).block();
    }

}
