package devarea.automatical;

import devarea.Main;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;

import java.io.Serializable;
import java.util.HashMap;

public class MessageSeria implements Serializable {

    private String idMessage;
    private String idChannel;

    public MessageSeria(Message message) {
        this.idMessage = message.getId().asString();
        this.idChannel = message.getChannelId().asString();
    }

    public MessageSeria(HashMap<String, String> map) {
        this.idMessage = map.get("idMessage");
        this.idChannel = map.get("idChannel");
        System.out.println("Créated : " + idMessage + " : " + idChannel);
    }

    public MessageSeria(String idMessage, String idChannel) {
        this.idMessage = idMessage;
        this.idChannel = idChannel;
    }

    public Message getMessage() {
        return ((TextChannel) Main.devarea.getChannelById(Snowflake.of(idChannel)).block()).getMessageById(Snowflake.of(this.idMessage)).block();
    }

    public HashMap<String, String> getHashMap() {
        HashMap<String, String> stock = new HashMap<>();
        stock.put("idMessage", idMessage);
        stock.put("idChannel", idChannel);
        return stock;
    }

    public boolean equalsTo(MessageSeria o) {
        return idMessage.equals(o.idMessage)
                && idChannel.equals(o.idChannel);
    }

    public boolean equalsTo(Message o) {
        return idMessage.equals(o.getId().asString()) && idChannel.equals(o.getChannelId().asString());
    }

    public Snowflake getMessageID() {
        return Snowflake.of(this.idMessage);
    }

    public Snowflake getChannelID() {
        return Snowflake.of(this.idChannel);
    }
}
