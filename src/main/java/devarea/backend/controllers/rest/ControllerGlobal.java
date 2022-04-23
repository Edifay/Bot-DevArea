package devarea.backend.controllers.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static devarea.backend.controllers.rest.requestContent.RequestHandlerGlobal.requestSendMessageToMember;

@CrossOrigin()
@RestController
public class ControllerGlobal {

    @GetMapping("global/send_message_by_discord")
    public static String[] sendMessageByDiscord(@RequestParam(value = "message_id", defaultValue = "null") String message_id, @RequestParam(value = "message", defaultValue = "msg") String message, @RequestParam(value = "code") String code) {
        return requestSendMessageToMember(message_id, message, code);
    }

}
