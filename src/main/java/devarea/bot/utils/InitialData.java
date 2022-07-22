package devarea.bot.utils;

import discord4j.common.util.Snowflake;

import java.util.HashMap;

public class InitialData {

    // Admin
    public String prefix = "//";
    public boolean vanish = false;

    // Emojis
    public Snowflake yes = null;
    public Snowflake no = null;
    public Snowflake loading = null;

    // Guilds
    public Snowflake devarea = Snowflake.of("768370886532137000");

    // Log Channels
    public Snowflake log_channel = null;
    public Snowflake logJoin_channel = null;

    // TextChannel
    public Snowflake paidMissions_channel = null;
    public Snowflake freeMissions_channel = null;
    public Snowflake presentation_channel = null;
    public Snowflake roles_channel = null;
    public Snowflake welcome_channel = null;
    public Snowflake general_channel = null;
    public Snowflake noMic_channel = null;
    public Snowflake meetupVerif_channel = null;
    public Snowflake meetupAnnounce_channel = null;
    public Snowflake bump_channel = null;
    public Snowflake command_channel = null;
    public Snowflake freelance_channel = null;

    // Voice Channel
    public Snowflake Help_voiceChannel = null;

    // Category
    public Snowflake join_category = null;
    public Snowflake missions_category = null;
    public Snowflake general_category = null;
    public Snowflake assistance_category = null;

    // Roles
    public Snowflake rulesAccepted_role = null;
    public Snowflake modo_role = null;
    public Snowflake admin_role = null;
    public Snowflake pingMeetup_role = null;
    public Snowflake devHelper_role = null;

    // Bots
    public Snowflake disboard_bot = null;

    // Assets
    public HashMap<String, String> assetsImages = null;

    // -> Badges
    public HashMap<String, String> badgesImages = null;

    public InitialData() {

    }
}
