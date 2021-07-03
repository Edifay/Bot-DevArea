package dependencies.auth.main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import dependencies.auth.domain.Connection;
import dependencies.auth.domain.Guild;
import dependencies.auth.domain.User;
import dependencies.auth.req.Post;
import discord4j.common.util.Snowflake;
import okhttp3.OkHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OAuthBuilder {

    private static final String BASEURI = "https://discordapp.com/api/";
    private static final String TOKENURI = "oauth2/token";
    private static final String REVOCATIONURI = "oauth2/token/revoke";

    private static final String CONNECTIONSURI = "users/@me/connections";
    private static final String MEURI = "users/@me";
    private static final String GUILDURI = "users/@me/guilds";

    //private static final String INVITEURI = "invites/{invite.id}";
    @JsonIgnore
    private OkHttpClient client;
    @JsonIgnore
    private String id;
    @JsonIgnore
    private String secret;
    @JsonProperty
    private String redirect;
    @JsonProperty
    private String scopes;
    @JsonProperty
    private String access_token;
    @JsonProperty
    private String refresh_token;
    @JsonIgnore
    private Snowflake idUser;

    @JsonProperty
    private long refresh_time;
    @JsonProperty
    private long last_time_refresh;
    @JsonIgnore
    private Thread t_refresh;

    public OAuthBuilder() {
        this.t_refresh = new Thread();
        this.client = new OkHttpClient();
    }

    public OAuthBuilder(String clientID, String clientSecret) {
        this.id = clientID;
        this.secret = clientSecret;

        this.t_refresh = new Thread();
        this.client = new OkHttpClient();
    }

    @JsonIgnore
    public OAuthBuilder setRedirectURI(String url) {
        this.redirect = url;
        return this;
    }

    @JsonIgnore
    public OAuthBuilder setScopes(String[] scopes) {
        this.scopes = "";
        for (String scope : scopes) {
            this.scopes += scope + "%20";
        }
        this.scopes = this.scopes.substring(0, this.scopes.length() - 3);
        return this;
    }

    @JsonIgnore
    public String getAuthorizationUrl(String state) {
        StringBuilder builder = new StringBuilder();

        builder.append(BASEURI);
        builder.append("oauth2/authorize");
        builder.append("?response_type=code");
        builder.append("&client_id=" + this.id);
        builder.append("&scope=" + this.scopes);
        if (state != null && state.length() > 0) builder.append("&state=" + state);
        builder.append("&redirect_uri=" + this.redirect);

        return builder.toString();
    }

    @JsonIgnore
    public Response exchange(String code) {
        try {
            String json = Post.exchangePost(client, BASEURI + TOKENURI, this.id, this.secret, code, this.redirect);

            JSONObject js = new JSONObject(json);

            try {
                this.access_token = js.getString("access_token");
                this.refresh_token = js.getString("refresh_token");
                this.refresh_time = js.getInt("expires_in") * 1000L;
                this.last_time_refresh = System.currentTimeMillis();

                this.idUser = Snowflake.of(this.getUser().getId());

                return Response.OK;
            } catch (JSONException e) {
                return Response.ERROR;
            }

        } catch (IOException e) {
            return Response.ERROR;
        }
    }

    @JsonIgnore
    public Response refresh() {
        try {
            String json = Post.refreshPost(client, BASEURI + TOKENURI, this.id, this.secret, this.refresh_token, this.redirect);

            JSONObject js = new JSONObject(json);

            try {
                this.access_token = js.getString("access_token");
                this.refresh_token = js.getString("refresh_token");
                this.refresh_time = js.getInt("expires_in") * 1000L;
                this.last_time_refresh = System.currentTimeMillis();

                return Response.OK;
            } catch (JSONException e) {
                return Response.ERROR;
            }

        } catch (IOException e) {
            return Response.ERROR;
        }
    }

    @JsonIgnore
    public Response revoke() {
        try {
            Post.revokePost(client, BASEURI + REVOCATIONURI, access_token);
            return Response.OK;
        } catch (IOException e) {
            return Response.ERROR;
        }
    }

    @JsonIgnore
    public User getUser() {
        User user = new User();

        try {
            String json = Post.get(client, BASEURI + MEURI, access_token);
            System.out.println("Get after request : " + json);
            JSONObject js = new JSONObject(json);
            try {
                user.setId(js.getString("id"));
                user.setAvatar(js.isNull("avatar") ? null : js.getString("avatar"));
                user.setBot(js.getBoolean("bot"));
                user.setDiscriminator(js.getString("discriminator"));
                user.setEmail(js.has("email") ? js.getString("email") : null);
                user.setMfa_enabled(js.getBoolean("mfa_enabled"));
                user.setUsername(js.getString("username"));
                user.setVerified(js.getBoolean("verified"));
            } catch (JSONException e) {
                user.setId(js.getString("id"));
                user.setAvatar(js.isNull("avatar") ? null : js.getString("avatar"));
                user.setDiscriminator(js.getString("discriminator"));
                user.setEmail(js.has("email") ? js.getString("email") : null);
                user.setUsername(js.getString("username"));
            }

        } catch (IOException e) {
            return null;
        }

        return user;
    }

    @JsonIgnore
    public List<Guild> getGuilds() {
        List<Guild> guilds = new ArrayList<>();

        try {
            String json = Post.get(client, BASEURI + GUILDURI, access_token);

            JSONArray arrJs = new JSONArray(json);

            for (Object guild : arrJs) {
                Guild g = new Guild();
                JSONObject obj = (JSONObject) guild;

                g.setIcon(obj.isNull("icon") ? null : obj.getString("icon"));
                g.setId(obj.getString("id"));
                g.setName(obj.getString("name"));
                g.setOwner(obj.getBoolean("owner"));
                g.setPermissions(obj.getInt("permissions"));

                guilds.add(g);
            }

        } catch (IOException e) {
            return null;
        }

        return guilds;
    }

    @JsonIgnore
    public List<Connection> getConnections() {
        List<Connection> connections = new ArrayList<>();

        try {
            String json = Post.get(client, BASEURI + CONNECTIONSURI, access_token);

            JSONArray arrJs = new JSONArray(json);

            for (Object connection : arrJs) {
                Connection c = new Connection();
                JSONObject obj = (JSONObject) connection;

                c.setFriend_sync(obj.getBoolean("friend_sync"));
                c.setId(obj.getString("id"));
                c.setName(obj.getString("name"));
                c.setType(obj.getString("type"));
                c.setVerified(obj.getBoolean("verified"));
                c.setVisibility(obj.getInt("visibility"));

                connections.add(c);
            }

        } catch (IOException e) {
            return null;
        }

        return connections;
    }

    @JsonIgnore
    public String getScopes() {
        return scopes;
    }

    @JsonIgnore
    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    @JsonIgnore
    public String getAccess_token() {
        return access_token;
    }

    @JsonIgnore
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @JsonIgnore
    public String getRefresh_token() {
        return refresh_token;
    }

    @JsonIgnore
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    @JsonIgnore
    public long getRefresh_time() {
        return this.refresh_time;
    }

    @JsonIgnore
    public void setRefresh_time(long refresh_time) {
        this.refresh_time = refresh_time;
    }

    @JsonIgnore
    public void setLast_time_refresh(long last_time_refresh) {
        this.last_time_refresh = last_time_refresh;
    }

    @JsonIgnore
    public long getLast_time_refresh() {
        return last_time_refresh;
    }

    @JsonIgnore
    public void enableAutoRefresh() {
        synchronized (this.t_refresh) {
            if (this.t_refresh.isAlive()) return;
            this.t_refresh = new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(120000L);
                        if (System.currentTimeMillis() - this.last_time_refresh < this.refresh_time - 600000L)
                            this.refresh();
                    }
                } catch (InterruptedException ignored) {
                }
            });
            this.t_refresh.start();
        }
    }

    @JsonIgnore
    public void disableAutoRefresh() {
        synchronized (this.t_refresh) {
            if (!this.t_refresh.isAlive()) return;
            this.t_refresh.interrupt();
            this.t_refresh = new Thread();
        }
    }

    @JsonIgnore
    public boolean isAlwaysEnable() {
        return System.currentTimeMillis() - this.last_time_refresh < this.refresh_time;
    }

    @JsonIgnore
    public void setLoaded(String clientID, String clientSecret) {
        this.id = clientID;
        this.secret = clientSecret;
    }

    @JsonIgnore
    public void setIdUser(Snowflake idUser) {
        this.idUser = idUser;
    }

    @JsonIgnore
    public Snowflake getIdUser() {
        return idUser;
    }

    @JsonSetter("idUser")
    public void setIdUser(String idUser) {
        this.idUser = Snowflake.of(idUser);
    }

    @JsonProperty("idUser")
    public String getIdUserString() {
        return this.idUser.asString();
    }
}
