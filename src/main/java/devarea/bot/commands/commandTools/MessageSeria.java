package devarea.bot.commands.commandTools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.global.cache.ChannelCache;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;

import java.io.Serializable;
import java.util.HashMap;

public class MessageSeria implements Serializable {

    @JsonProperty("idMessage")
    private String idMessage;
    @JsonProperty("idChannel")
    private String idChannel;

    public MessageSeria(Message message) {
        this.idMessage = message.getId().asString();
        this.idChannel = message.getChannelId().asString();
    }

    public MessageSeria(HashMap<String, String> map) {
        this.idMessage = map.get("idMessage");
        this.idChannel = map.get("idChannel");
    }

    public MessageSeria(@JsonProperty("idMessage") String idMessage, @JsonProperty("idChannel") String idChannel) {
        this.idMessage = idMessage;
        this.idChannel = idChannel;
    }

    @JsonIgnore
    public Message getMessage() {
        return ((TextChannel) ChannelCache.get(idChannel)).getMessageById(Snowflake.of(this.idMessage)).block();
    }

    @JsonIgnore
    public HashMap<String, String> getHashMap() {
        HashMap<String, String> stock = new HashMap<>();
        stock.put("idMessage", idMessage);
        stock.put("idChannel", idChannel);
        return stock;
    }

    @JsonIgnore
    public boolean equalsTo(MessageSeria o) {
        return idMessage.equals(o.idMessage)
                && idChannel.equals(o.idChannel);
    }

    @JsonIgnore
    public boolean equalsTo(Message o) {
        return idMessage.equals(o.getId().asString()) && idChannel.equals(o.getChannelId().asString());
    }

    @JsonIgnore
    public Snowflake getMessageID() {
        return Snowflake.of(this.idMessage);
    }

    @JsonIgnore
    public Snowflake getChannelID() {
        return Snowflake.of(this.idChannel);
    }
}
