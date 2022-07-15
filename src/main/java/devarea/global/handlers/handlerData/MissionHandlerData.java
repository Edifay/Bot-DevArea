package devarea.global.handlers.handlerData;

import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.bot.commands.commandTools.MessageSeria;
import devarea.bot.commands.commandTools.Mission;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MissionHandlerData {
    @JsonProperty("mission_follow_id")
    public int missionFollowId;
    @JsonProperty("missions_follow")
    public ArrayList<MissionFollow> missionsFollow;

    public MissionHandlerData(final int missionFollowId,
                              final ArrayList<MissionFollow> missionsFollow) {
        this.missionFollowId = missionFollowId;
        this.missionsFollow = missionsFollow;
    }

    public MissionHandlerData() {
    }

    public static class MissionFollow {
        @JsonProperty("mission_id")
        public int missionID;
        @JsonProperty("message")
        public MessageSeria messageSeria;
        @JsonProperty("client_id")
        public String clientID;
        @JsonProperty("dev_id")
        public String devID;

        public MissionFollow(int missionID, final MessageSeria messageSeria, final String clientID,
                             final String devID) {
            this.missionID = missionID;
            this.messageSeria = messageSeria;
            this.clientID = clientID;
            this.devID = devID;
        }

        public MissionFollow() {

        }

    }
}
