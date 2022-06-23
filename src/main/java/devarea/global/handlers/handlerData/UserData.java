package devarea.global.handlers.handlerData;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserData {

    public String userDescription;
    public HashMap<String, Integer> xpGainHistory = new HashMap<>();

    public UserData() {

    }

    public HashMap<String, Integer> getXpGainHistory() {
        if (xpGainHistory.size() == 0)
            return null;
        return xpGainHistory;
    }
}