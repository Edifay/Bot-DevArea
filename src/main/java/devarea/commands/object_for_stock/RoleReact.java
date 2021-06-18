package devarea.commands.object_for_stock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.Main;
import devarea.automatical.MessageSeria;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.HashMap;

public class RoleReact {

    String reactionId;
    String isID;
    MessageSeria message;

    public RoleReact(Snowflake reactionId, Message message) {
        this.reactionId = reactionId.asString();
        this.message = new MessageSeria(message);
        this.isID = "true";
    }

    public RoleReact(String reactionId, Message message, String isID) {
        this.reactionId = reactionId;
        this.message = new MessageSeria(message);
        this.isID = isID;
    }

    public RoleReact(Snowflake reactionId, MessageSeria message) {
        this.reactionId = reactionId.asString();
        this.message = message;
        this.isID = "true";
    }

    public RoleReact(String reactionId, MessageSeria message, String isID) {
        this.reactionId = reactionId;
        this.message = message;
        this.isID = isID;
    }

    public RoleReact(HashMap<String, String> map) {
        try {
            reactionId = map.get("reactionId");
            ObjectMapper mapper = new ObjectMapper();
            String str = map.get("message").replace("=", "\":\"").replace(", ", "\", \"").replace("{", "{\"").replace("}", "\"}");
            message = new MessageSeria(mapper.readValue(str, new TypeReference<HashMap<String, String>>() {
            }));
            isID = map.get("isID");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getHashMap() {
        HashMap<String, String> stock = new HashMap<>();
        stock.put("reactionId", reactionId);
        stock.put("message", message.getHashMap().toString());
        stock.put("isID", isID);
        return stock;
    }

    public boolean is(ReactionAddEvent event) {
        return message.equalsTo(new MessageSeria(event.getMessageId().asString(), event.getChannelId().asString())) && reactionId.equals(event.getEmoji().asCustomEmoji().isPresent() ? event.getEmoji().asCustomEmoji().get().getId().asString() : event.getEmoji().asUnicodeEmoji().get().getRaw());
    }

    public boolean is(ReactionRemoveEvent event) {
        return message.equalsTo(new MessageSeria(event.getMessageId().asString(), event.getChannelId().asString())) && reactionId.equals(event.getEmoji().asCustomEmoji().isPresent() ? event.getEmoji().asCustomEmoji().get().getId().asString() : event.getEmoji().asUnicodeEmoji().get().getRaw());
    }

    public ReactionEmoji getEmoji() {
        if (isID.equals("true")) {
            return ReactionEmoji.custom(Main.devarea.getGuildEmojiById(Snowflake.of(this.reactionId)).block());
        } else {
            return ReactionEmoji.unicode(this.reactionId);
        }
    }

    public boolean isID() {
        return this.isID.equals("true");
    }

    public String getStringEmoji() {
        return this.isID() ? "<:ayy:" + this.reactionId + ">" : this.reactionId;
    }

    public MessageSeria getMessageSeria(){
        return this.message;
    }

    public void delete(){
        message.getMessage().removeReactions(this.getEmoji()).subscribe();
    }

}
