package devarea.global.handlers.handlerData;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserData {

    public String userDescription;

    public UserData() {

    }
}