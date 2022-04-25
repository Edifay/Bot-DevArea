package devarea.bot.automatical;

import devarea.bot.Init;
import devarea.bot.cache.MemberCache;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.GuildMemberEditSpec;
import discord4j.core.spec.VoiceChannelCreateSpec;
import discord4j.discordjson.possible.Possible;

import java.util.Objects;
import java.util.Optional;

public class VoiceChannelHandler {

    private static int number = 1;

    public static void join(VoiceStateUpdateEvent event) {
        try {
            if (event.getCurrent().getChannelId().get().equals(Init.idVoiceChannelHelp)) {
                Snowflake id = Objects.requireNonNull(Init.devarea.createVoiceChannel(VoiceChannelCreateSpec.builder()
                        .name("Aide #" + number)
                        .parentId(Init.idCategoryGeneral)
                        .userLimit(5)
                        .build()).block()).getId();
                Objects.requireNonNull(MemberCache.get(event.getCurrent().getUserId().asString())).edit(
                        GuildMemberEditSpec.builder()
                                .newVoiceChannel(Possible.of(Optional.of(id)))
                                .build()
                ).subscribe();
            }
            if (event.getOld().isPresent())
                leave(event);
        } catch (Exception ignored) {
        }
    }

    public static void leave(VoiceStateUpdateEvent event) {
        if (event.getOld().isEmpty() || event.getOld().get().getChannelId().isEmpty())
            return;

        VoiceChannel channel = event.getOld().get().getChannel().block();
        if (channel.getVoiceStates().buffer().blockLast() == null && channel.getName().startsWith("Aide")) {
            number--;
            try {
                channel.delete().subscribe();
            } catch (Exception ignored) {
            }
        }
    }

}
