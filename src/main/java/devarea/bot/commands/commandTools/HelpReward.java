package devarea.bot.commands.commandTools;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class HelpReward {
    @JsonProperty("memberId")
    private final String memberId;

    @JsonProperty("helpersIds")
    private final List<String> helpersIds;

    @JsonProperty("dateTime")
    private final LocalDateTime dateTime;

    public HelpReward(String memberId, List<String> helpersIds) {
        this.memberId = memberId;
        this.helpersIds = helpersIds;
        this.dateTime = LocalDateTime.now();
    }

    public String getMemberId() {
        return memberId;
    }

    public List<String> getHelpersIds() {
        return helpersIds;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
