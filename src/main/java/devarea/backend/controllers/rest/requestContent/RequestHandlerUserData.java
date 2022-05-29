package devarea.backend.controllers.rest.requestContent;

import devarea.backend.controllers.handlers.UserInfosHandlers;
import devarea.backend.controllers.tools.userInfos.WebPrivateUserInfos;
import devarea.backend.controllers.tools.userInfos.WebPublicUserInfos;

public class RequestHandlerUserData {

    public static WebPublicUserInfos requestGetMemberProfile(String id) {
        return (WebPublicUserInfos) new WebPublicUserInfos(id).update();
    }

    public static boolean requestUpdateUserDescription(String description, String code) {
        WebPrivateUserInfos infos = RequestHandlerAuth.get(code);
        if (infos != null) {
            if (description != null && description.length() > 300)
                description = description.substring(0, 300);
            UserInfosHandlers.get(infos.getId()).userDescription = description;
            UserInfosHandlers.updated();
            return true;
        }
        return false;
    }
}