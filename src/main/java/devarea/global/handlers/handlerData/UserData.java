package devarea.global.handlers.handlerData;

import com.fasterxml.jackson.annotation.JsonInclude;
import devarea.bot.commands.commandTools.FreeLance;
import devarea.bot.commands.commandTools.Mission;

import java.util.LinkedHashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserData {

    public String userDescription;
    public Integer xp;
    public FreeLance freeLance;
    public LinkedHashMap<String, Integer> xpGainHistory = new LinkedHashMap<>();
    public LinkedHashMap<String, Mission> missions = new LinkedHashMap<>();

    public UserData() {

    }

    public LinkedHashMap<String, Integer> getXpGainHistory() {
        if (xpGainHistory.size() == 0)
            return null;
        return xpGainHistory;
    }

    public LinkedHashMap<String, Mission> getMissions() {
        if (missions.size() == 0)
            return null;
        return missions;
    }
}