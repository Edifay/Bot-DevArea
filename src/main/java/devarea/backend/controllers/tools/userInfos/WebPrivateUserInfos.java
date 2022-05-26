package devarea.backend.controllers.tools.userInfos;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebPrivateUserInfos extends WebUserInfos {

    public WebPrivateUserInfos(String id) {
        super(id);
    }
}
