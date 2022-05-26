package devarea.backend.controllers.rest;

import devarea.backend.controllers.tools.userInfos.WebPrivateUserInfos;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static devarea.backend.controllers.rest.requestContent.RequestHandlerAuth.requestDeleteAccount;
import static devarea.backend.controllers.rest.requestContent.RequestHandlerAuth.requestGetUserInfo;


@CrossOrigin()
@RestController
public class ControllerAuth {

    @GetMapping("/auth/get")
    public static WebPrivateUserInfos getUserInfo(@RequestParam(value = "code") final String code) throws IOException {
        return requestGetUserInfo(code);
    }

    @GetMapping("/auth/delete-account")
    public static boolean removeBinding(@RequestParam(value = "code") final String code) throws IOException {
        return requestDeleteAccount(code);
    }
}
