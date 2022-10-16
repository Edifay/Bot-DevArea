package devarea.bot.commands;

import devarea.bot.presets.ColorsUsed;
import devarea.global.cache.ChannelCache;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionReplyEditSpec;

import static devarea.global.utils.ThreadHandler.startAway;

public abstract class LongCommand extends Command {

    protected Message lastMessage;
    protected FirstStep firstStep;
    protected boolean isLocalChannel;

    /*
        600000ms -> 10min
     */
    protected long AUTO_CLOSE_TIME = 600000L;

    public LongCommand() {
        super();
    }

    public LongCommand(final Member member) {
        super(member);
        this.isLocalChannel = false;
        this.deletedCommand(AUTO_CLOSE_TIME);
    }

    public LongCommand(final Member member, final GuildMessageChannel channel) {
        super(member, channel);
        this.isLocalChannel = false;
        this.deletedCommand(AUTO_CLOSE_TIME);
    }

    public LongCommand(final Member member, final ChatInputInteractionEvent chatInteraction) {
        super(member, chatInteraction);
        chatInteraction.deferReply().subscribe();
        this.isLocalChannel = false;
        this.deletedCommand(AUTO_CLOSE_TIME);
    }

    public void nextStep(final ReactionAddEvent event) {
        try {

            // Verify the validity of the Reaction
            Message message = event.getMessage().block();
            if (!message.getId().equals(this.lastMessage.getId())) {
                sendErrorYouAreInCommand(event.getChannelId().asString());
                message.removeReaction(event.getEmoji(), event.getUserId()).subscribe();
                return;
            }

            // Call the Step
            if (this.firstStep.receiveReact(event)) {
                this.endCommand();
            }

            // Shadow
            message.removeReaction(event.getEmoji(), event.getUserId()).block();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nextStep(final MessageCreateEvent event) {
        try {
            // Verify the validity of the Message
            if (!event.getMessage().getChannelId().equals(this.channel.getId())) {
                sendErrorYouAreInCommand(event.getMessage().getChannelId().asString());
                delete(false, event.getMessage());
                return;
            }

            // Check if it's cancel call
            String content = event.getMessage().getContent().toLowerCase();
            if (content.startsWith("cancel") || content.startsWith("annuler")) {
                delete(false, event.getMessage());
                this.removeTrace();
                return;
            }

            // Call the Step
            if (this.firstStep.receiveMessage(event))
                this.endCommand();

            // Shadow
            delete(false, event.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nextStep(final ButtonInteractionEvent event) {
        synchronized (this) {
            try {
                // Verify the validity of the Interaction
                if (!event.getMessage().get().getChannelId().equals(this.channel.getId())) {
                    sendErrorYourAreInCommand(event);
                    return;
                }

                // Call the Step
                if (this.firstStep.receiveInteract(event))
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

    protected void deletedCommand(final long millis, final Runnable runnable) {
        if (this.deletedCommandThread != null && this.deletedCommandThread.isAlive())
            this.deletedCommandThread.interrupt();

        this.deletedCommandThread = new Thread(() -> {
            try {
                Thread.sleep(millis);
                runnable.run();
                this.removeTrace();
            } catch (InterruptedException e) {
            }
        });
        this.deletedCommandThread.start();
    }

    protected void deletedCommand(final long millis) {
        this.deletedCommand(millis, () -> {
        });
    }

    protected void endCommand() {
        startAway(() -> {
            if (this.isLocalChannel) {
                try {
                    this.channel.delete().subscribe(chanl -> {

                    }, error -> {
                        System.err.println("ERROR: Le localChannel n'a pas pu être supprimé !");
                    });
                } catch (Exception e) {
                }
            }
            if (this.deletedCommandThread != null && this.deletedCommandThread.isAlive())
                this.deletedCommandThread.interrupt();
            if (chatInteraction != null && this.firstStep != null)
                delete(false, this.firstStep.message);
            CommandManager.removeCommand(this.member.getId(), this);
        });
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

    private void sendErrorYouAreInCommand(String channelId) {
        startAway(() -> deletedEmbed((GuildMessageChannel) ChannelCache.watch(channelId),
                EmbedCreateSpec.builder()
                        .title("Erreur !")
                        .description("Vous avez une commande en cours dans <#" + this.channel.getId().asString() + ">")
                        .color(ColorsUsed.wrong).build()
        ));

    }

    /*
        With ephemeral
     */
    private void sendErrorYourAreInCommand(ButtonInteractionEvent event) {
        event.reply(InteractionApplicationCommandCallbackSpec.builder().addEmbed(EmbedCreateSpec.builder()
                                .title("Erreur !")
                                .description("Vous avez une commande en cours dans <#" + this.channel.getId().asString() + ">")
                                .color(ColorsUsed.wrong).build())
                        .ephemeral(true).build())
                .subscribe();
    }
}