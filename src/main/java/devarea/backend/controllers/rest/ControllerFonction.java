package devarea.backend.controllers.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ControllerFonction {

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

    public static final class PasswordGenerator {

        private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
        private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private static final String DIGITS = "0123456789";
        private static final String PUNCTUATION = "!@#$%&*()_+-=[]|,./?><";
        private boolean useLower;
        private boolean useUpper;
        private boolean useDigits;
        private boolean usePunctuation;

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
