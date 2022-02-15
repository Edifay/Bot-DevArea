package devarea.bot.commands;

import devarea.bot.data.ColorsUsed;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.EmbedCreateSpec;

import static devarea.bot.event.FunctionEvent.startAway;

public abstract class LongCommand extends Command {

    protected Message lastMessage;
    protected FirstStape firstStape;
    protected boolean isLocalChannel;

    public LongCommand() {
        super();
    }

    public LongCommand(final MessageCreateEvent message) {
        super(message);
        this.isLocalChannel = false;
    }

    public LongCommand(final ReactionAddEvent event) {
        super(event);
        this.isLocalChannel = false;
    }

    public LongCommand(final Member member) {
        super(member);
    }

    public void nextStape(final ReactionAddEvent event) {
        synchronized (this) {
            try {
                Message message = event.getMessage().block();
                if (!message.getId().equals(this.lastMessage.getId())) {
                    deletedEmbed((TextChannel) message.getChannel().block(), EmbedCreateSpec.builder()
                            .title("Error !")
                            .description("Vous avez une commande en cours dans <#" + this.channel.getId().asString() + ">")
                            .color(ColorsUsed.wrong).build());
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
                    startAway(() -> deletedEmbed((TextChannel) event.getMessage().getChannel().block(), EmbedCreateSpec.builder()
                            .title("Error !")
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

    protected void removeTrace() {
        sendError("Vous avez annuler la commande !");
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