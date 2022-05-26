package devarea.backend.controllers.rest.requestContent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devarea.backend.controllers.tools.userInfos.WebPrivateUserInfos;
import devarea.backend.controllers.tools.userInfos.WebPublicUserInfos;
import devarea.bot.Init;
import devarea.bot.presets.ColorsUsed;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.legacy.LegacyMessageCreateSpec;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static devarea.bot.event.FunctionEvent.startAway;

public class RequestHandlerGlobal {

    public static ArrayList<String> couldown = new ArrayList<>();

    public static String[] requestSendMessageToMember(String message_id, String message, String code) {
        WebPrivateUserInfos user = RequestHandlerAuth.get(code);
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
                    member.getPrivateChannel().block().createMessage(message).subscribe();
                } else {
                    member.getPrivateChannel().block().createMessage(getMessageForId(Integer.parseInt(message_id),
                            message)).subscribe();
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

    public final static ObjectMapper mapper = new ObjectMapper();

    public static Object[] getObjectsFromJson(final String url, TypeReference reference) throws FileNotFoundException {
        File file = new File(url); // check if file exist ! And create it if not !
        if (!file.exists()) {
            PrintStream out = new PrintStream(file);
            out.print("[]");
            out.flush();
            out.close();
        }

        try (InputStream input = new FileInputStream(url)) { // load file !

            String data = StreamUtils.copyToString(input, StandardCharsets.UTF_8);
            Object[] objects = (Object[]) mapper.readValue(data, reference);

            return objects;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Consumer<? super LegacyMessageCreateSpec> getMessageForId(int id, String message) {
        switch (id) {
            case 455:
                return msg -> {
                    msg.addEmbed(embed -> {
                        embed.setTitle("Comment créer une mission ?");
                        embed.setColor(ColorsUsed.same);
                        embed.setDescription(
                                "Rien de plus simple ! Il faut se rendre sur le channel Missions-Payantes de Dev'Area" +
                                        " (<#" + Init.idMissionsPayantes.asString() + ">)." +
                                        "\n\nPuis réagir au message proposant de créer sa mission, et suivre les " +
                                        "étapes de créations.\n\nEn espérant t'aider !");
                    });
                };
            default:
                return msg -> msg.setContent("Default Message");
        }
    }

    public static final class PasswordGenerator {

        private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
        private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private static final String DIGITS = "0123456789";
        private static final String PUNCTUATION = "!@#$%&*()_+-=[]|,./?><";
        private final boolean useLower;
        private final boolean useUpper;
        private final boolean useDigits;
        private final boolean usePunctuation;

        private PasswordGenerator() {
            throw new UnsupportedOperationException("Empty constructor is not supported.");
        }

        public PasswordGenerator(PasswordGeneratorBuilder builder) {
            this.useLower = builder.useLower;
            this.useUpper = builder.useUpper;
            this.useDigits = builder.useDigits;
            this.usePunctuation = builder.usePunctuation;
        }

        public static class PasswordGeneratorBuilder {

            private boolean useLower;
            private boolean useUpper;
            private boolean useDigits;
            private boolean usePunctuation;

            public PasswordGeneratorBuilder() {
                this.useLower = false;
                this.useUpper = false;
                this.useDigits = false;
                this.usePunctuation = false;
            }

            public PasswordGeneratorBuilder useLower(boolean useLower) {
                this.useLower = useLower;
                return this;
            }

            public PasswordGeneratorBuilder useUpper(boolean useUpper) {
                this.useUpper = useUpper;
                return this;
            }

            public PasswordGeneratorBuilder useDigits(boolean useDigits) {
                this.useDigits = useDigits;
                return this;
            }

            public PasswordGeneratorBuilder usePunctuation(boolean usePunctuation) {
                this.usePunctuation = usePunctuation;
                return this;
            }

            public PasswordGenerator build() {
                return new PasswordGenerator(this);
            }
        }

        public String generate(int length) {
            if (length <= 0)
                return "";

            StringBuilder password = new StringBuilder(length);
            Random random = new Random(System.nanoTime());

            List<String> charCategories = new ArrayList<>(4);
            if (useLower)
                charCategories.add(LOWER);
            if (useUpper)
                charCategories.add(UPPER);
            if (useDigits)
                charCategories.add(DIGITS);
            if (usePunctuation)
                charCategories.add(PUNCTUATION);

            for (int i = 0; i < length; i++) {
                String charCategory = charCategories.get(random.nextInt(charCategories.size()));
                int position = random.nextInt(charCategory.length());
                password.append(charCategory.charAt(position));
            }

            return new String(password);
        }
    }


}
