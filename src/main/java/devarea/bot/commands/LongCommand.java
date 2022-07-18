package devarea.bot.commands;

import devarea.global.cache.ChannelCache;
import devarea.bot.presets.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionReplyEditSpec;

import static devarea.bot.event.FunctionEvent.startAway;

public abstract class LongCommand extends Command {

    protected Message lastMessage;
    protected FirstStape firstStape;
    protected boolean isLocalChannel;

    public LongCommand() {
        super();
    }

    public LongCommand(final Member member) {
        super(member);
        this.isLocalChannel = false;
    }

    public LongCommand(final Member member, final TextChannel channel) {
        super(member, channel);
        this.isLocalChannel = false;
    }

    public LongCommand(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        this.isLocalChannel = false;
    }

    public void nextStape(final ReactionAddEvent event) {
        synchronized (this) {
            try {
                Message message = event.getMessage().block();
                if (!message.getId().equals(this.lastMessage.getId())) {
                    deletedEmbed((TextChannel) ChannelCache.watch(message.getChannelId().asString()),
                            EmbedCreateSpec.builder()
                                    .title("Erreur !")
                                    .description("Vous avez une commande en cours dans <#" + this.channel.getId().asString() + ">")
                                    .color(ColorsUsed.wrong).build());
                    message.removeReaction(event.getEmoji(), event.getUserId()).subscribe();
                    return;
                }
                if (this.firstStape.receiveReact(event)) {
                    this.endCommand();
                }
                try {
                    message.removeReaction(event.getEmoji(), event.getUserId()).block();
                } catch (Exception e) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void nextStape(final MessageCreateEvent event) {
        synchronized (this) {
            try {
                if (!event.getMessage().getChannelId().equals(this.channel.getId())) {
                    startAway(() -> deletedEmbed((TextChannel) ChannelCache.watch(event.getMessage().getChannelId().asString()),
                            EmbedCreateSpec.builder()
                                    .title("Erreur !")
                                    .description("Vous avez une commande en cours dans <#" + this.channel.getId().asString() + ">")
                                    .color(ColorsUsed.wrong).build()
                    ));
                    delete(false, event.getMessage());
                    return;
                }
                if (event.getMessage().getContent().toLowerCase().startsWith("cancel") || event.getMessage().getContent().toLowerCase().startsWith("annuler"))
                    this.removeTrace();
                else if (this.firstStape.receiveMessage(event))
                    this.endCommand();
                delete(false, event.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void nextStape(final ButtonInteractionEvent event) {
        synchronized (this) {
            try {
                if (!event.getMessage().get().getChannelId().equals(this.channel.getId())) {
                    event.reply(InteractionApplicationCommandCallbackSpec.builder().addEmbed(EmbedCreateSpec.builder()
                            .title("Erreur !")
                            .description("Vous avez une commande en cours dans <#" + this.channel.getId().asString() + ">")
                            .color(ColorsUsed.wrong).build()).ephemeral(true).build()).subscribe();
                    return;
                }
                if (event.getMessage().get().getContent().toLowerCase().startsWith("cancel") || event.getMessage().get().getContent().toLowerCase().startsWith("annuler"))
                    this.removeTrace();
                else if (this.firstStape.receiveInteract(event))
                    this.endCommand();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void removeTrace() {
        if (chatInteraction != null)
            chatInteraction.editReply(InteractionReplyEditSpec.builder()
                    .addEmbed(EmbedCreateSpec.builder()
                            .title("Erreur !")
                            .description("Vous avez annulé la commande")
                            .color(ColorsUsed.wrong)
                            .build())
                    .build()).subscribe();
        else
            sendError("Vous avez annulé la commande !");
        delete(false, this.lastMessage);
        endCommand();
    }

    @Override
    protected Boolean endCommand() {
        if (this.isLocalChannel) {
            try {
                this.channel.delete().subscribe(chanl -> {

                }, error -> {
                    System.err.println("ERROR: Le localChannel n'a pas pu être supprimé !");
                });
            } catch (Exception e) {
            }
        }
        return super.endCommand();
    }

    @Override
    protected boolean createLocalChannel(String name, Snowflake parentId) {
        this.isLocalChannel = super.createLocalChannel(name, parentId);
        return this.isLocalChannel;
    }

    @Override
    protected boolean createLocalChannel(String name, Snowflake parentId, boolean canWrite) {
        this.isLocalChannel = super.createLocalChannel(name, parentId, canWrite);
        return this.isLocalChannel;
    }
}