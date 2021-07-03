package dependencies.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    private String id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String discriminator;
    @JsonIgnore
    private String avatar;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    @JsonIgnore
    private boolean bot;
    @JsonIgnore
    private boolean mfa_enabled;
    @JsonIgnore
    private boolean verified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    @JsonProperty("urlAvatar")
    public String getAvatar() {
        return "https://cdn.discordapp.com/avatars/"+id+"/"+avatar+".webp?size=256";
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    public boolean isMfa_enabled() {
        return mfa_enabled;
    }

    public void setMfa_enabled(boolean mfa_enabled) {
        this.mfa_enabled = mfa_enabled;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public String toString() {
        return this.username + " # " + this.discriminator
                + "\nID: " + this.id
                + "\nAvatar: " + this.avatar
                + "\nIsBot: " + this.bot
                + "\nMFA: " + this.mfa_enabled
                + "\nEmail: " + this.email
                + "\nVerified: " + this.verified;
    }


}
