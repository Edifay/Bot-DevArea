package devarea.automatical;

import devarea.Main;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;

import java.io.Serializable;

public class MessageSeria implements Serializable {

    private String idMessage;
    private String idChannel;

    public MessageSeria(Message message) {
        this.idMessage = message.getId().asString();
        this.idChannel = message.getChannelId().asString();
    }

    public Message getMessage() {
        return ((TextChannel) Main.devarea.getChannelById(Snowflake.of(idChannel)).block()).getMessageById(Snowflake.of(this.idMessage)).block();
    }

}
