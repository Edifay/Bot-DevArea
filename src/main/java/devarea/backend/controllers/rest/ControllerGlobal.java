package devarea.backend.controllers.rest;

import devarea.backend.controllers.data.UserInfo;
import devarea.bot.data.ColorsUsed;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.legacy.LegacyMessageCreateSpec;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.function.Consumer;

import static devarea.bot.event.FunctionEvent.startAway;

@CrossOrigin()
@RestController
public class ControllerGlobal {

    public static ArrayList<String> couldown = new ArrayList<>();

    @GetMapping("global/send_message_by_discord")
    public static String[] sendMessageByDiscord(@RequestParam(value = "message_id", defaultValue = "null") String message_id, @RequestParam(value = "message", defaultValue = "msg") String message, @RequestParam(value = "code") String code) {
        UserInfo user = ControllerOAuth2.getInfoFor(code);
        if (user != null && !couldown.contains(code)) {
            couldown.add(code);
            startAway(() -> {
                try {
                    Thread.sleep(5000);
                    couldown.remove(code);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            try {
                Member member = user.getMember();
                if (message_id.equals("null")) {
                    member.getPrivateChannel().block().createMessage(message).block();
                } else {
                    member.getPrivateChannel().block().createMessage(getMessageForId(Integer.parseInt(message_id), message)).block();
                }

            } catch (Exception e) {
                return new String[]{"send_error"};
            }
            return new String[]{"send"};
        } else {
            if (couldown.contains(code)) {
                return new String[]{"couldown"};
            } else {
                return new String[]{"wrong_code"};
            }
        }

    }

    public static Consumer<? super LegacyMessageCreateSpec> getMessageForId(int id, String message) {
        switch (id) {
            case 455:
                return msg -> {
                    msg.addEmbed(embed -> {
                        embed.setTitle("Comment créer une mission ?");
                        embed.setColor(ColorsUsed.same);
                        embed.setDescription("Rien de plus simple ! Il faut se rendre sur le channel Missions-Payantes de Dev'Area (<#768855632224190496>)." +
                                "\n\nPuis réagir au message proposant de créer sa mission, et suivre les étapes de créations.\n\nEn espérant t'aider !");
                    });
                };
            default:
                return msg -> msg.setContent("Default Message");
        }
    }

}
