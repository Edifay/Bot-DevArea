package devarea.bot.commands.inLine;

import devarea.bot.Init;
import devarea.bot.commands.ShortCommand;
import devarea.bot.commands.SlashCommand;
import devarea.global.cache.ChannelCache;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.ThreadChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.util.AllowedMentions;

import java.util.ArrayList;

import static devarea.global.utils.ThreadHandler.startAway;
import static devarea.global.utils.ThreadHandler.startAwayIn;

public class DevHelp extends ShortCommand implements SlashCommand {

    public final static ArrayList<String> listOfForums;

    static {
        listOfForums = new ArrayList<>();
        listOfForums.add("1023491568130326610");
        listOfForums.add("1020727236984307822");
    }

    private static final ArrayList<Snowflake> timer = new ArrayList<>();

    public DevHelp(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        if (channel instanceof ThreadChannel && ((ThreadChannel) channel).getParentId().isPresent() && listOfForums.contains(((ThreadChannel) channel).getParentId().get().asString()) || channel instanceof TextChannel && ((TextChannel) channel).getCategoryId().isPresent() && ((TextChannel) channel).getCategoryId().get().equals(Init.initial.assistance_category)) {
            if (!timer.contains(this.channel.getId())) {
                startAway(() -> {
                    chatInteraction.deferReply().block();
                    chatInteraction.deleteReply().subscribe();
                });
                ((GuildMessageChannel) ChannelCache.watch(chatInteraction.getInteraction().getChannelId().asString())).createMessage(
                        MessageCreateSpec.builder()
                                .allowedMentions(AllowedMentions.builder()
                                        .allowRole(Init.initial.devHelper_role)
                                        .allowUser(member.getId())
                                        .build())
                                .content("<@" + this.member.getId().asString() + ">, a demandé de " +
                                        "l'aide ! <@&" + Init.initial.devHelper_role.asString() + ">.").build()
                ).subscribe();
                timer.add(this.channel.getId());
                startAwayIn(() -> timer.remove(channel.getId()), 1800000, false);
            } else
                replyError("La commande devhelp n'est disponible que toutes les 30 minutes.");
        } else
            replyError("Uniquement les channels d'entraide acceptent cette commande.");
    }

    public DevHelp() {
    }

    @Override
    public ApplicationCommandRequest getSlashCommandDefinition() {
        return ApplicationCommandRequest.builder()
                .name("devhelp")
                .description("Commande qui permet d'envoyer un ping au développeurs volontaires dans les channels " +
                        "entraides.")
                .build();
    }
}
