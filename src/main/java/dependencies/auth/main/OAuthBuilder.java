package dependencies.auth.main;

import dependencies.auth.domain.User;
import dependencies.auth.req.Post;
import discord4j.common.util.Snowflake;
import okhttp3.OkHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OAuthBuilder {

    private static final String BASEURI = "https://discordapp.com/api/";
    private static final String TOKENURI = "oauth2/token";
    private static final String REVOCATIONURI = "oauth2/token/revoke";

    private static final String MEURI = "users/@me";

    private OkHttpClient client;
    private String id, secret, redirect, scopes, access_token, refresh_token, idUser;

    private long refresh_time, last_time_refresh;
    private Thread t_refresh;

    public OAuthBuilder(String clientID, String clientSecret) {
        this.id = clientID;
        this.secret = clientSecret;

        this.t_refresh = new Thread();
        this.client = new OkHttpClient();
    }

    public OAuthBuilder setRedirectURI(String url) {
        this.redirect = url;
        return this;
    }
    public OAuthBuilder setScopes(String[] scopes) {
        this.scopes = "";
        for (String scope : scopes) {
            this.scopes += scope + "%20";
        }
        this.scopes = this.scopes.substring(0, this.scopes.length() - 3);
        return this;
    }
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
    public Response exchange(String code) {
        try {
            String json = Post.exchangePost(client, BASEURI + TOKENURI, this.id, this.secret, code, this.redirect);

            JSONObject js = new JSONObject(json);

            try {
                this.access_token = js.getString("access_token");
                this.refresh_token = js.getString("refresh_token");
                this.refresh_time = js.getInt("expires_in") * 1000L;
                this.last_time_refresh = System.currentTimeMillis();

                this.idUser = Snowflake.of(this.getUser().getId()).asString();

                return Response.OK;
            } catch (JSONException e) {
                return Response.ERROR;
            }

        } catch (IOException e) {
            return Response.ERROR;
        }
    }
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
    public Response revoke() {
        try {
            Post.revokePost(client, BASEURI + REVOCATIONURI, access_token);
            return Response.OK;
        } catch (IOException e) {
            return Response.ERROR;
        }
    }
    public User getUser() {
        User user = new User();

        try {
            String json = Post.get(client, BASEURI + MEURI, access_token);
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
    public String getAccess_token() {
        return access_token;
    }
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
    public String getRefresh_token() {
        return refresh_token;
    }
    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
    public long getRefresh_time() {
        return this.refresh_time;
    }
    public void setRefresh_time(long refresh_time) {
        this.refresh_time = refresh_time;
    }
    public void setLast_time_refresh(long last_time_refresh) {
        this.last_time_refresh = last_time_refresh;
    }
    public long getLast_time_refresh() {
        return last_time_refresh;
    }

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
    public void disableAutoRefresh() {
        synchronized (this.t_refresh) {
            if (!this.t_refresh.isAlive()) return;
            this.t_refresh.interrupt();
            this.t_refresh = new Thread();
        }
    }

    public boolean isAlwaysUseful() {
        return System.currentTimeMillis() - this.last_time_refresh < this.refresh_time;
    }

    public String getIdUser() {
        return idUser;
    }
    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

}