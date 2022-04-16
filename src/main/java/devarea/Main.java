package devarea;

import devarea.backend.SpringBackend;
import devarea.bot.Init;
import org.springframework.boot.SpringApplication;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

public class Main {

    public static final boolean developing = false;

    public static final String domainName = "https://devarea.fr/";

    public static void main(String[] args) {

        if (!developing)
            try {
                PrintStream out = new PrintStream("out.txt");
                System.setOut(out);
                System.setErr(out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        SpringApplication.run(SpringBackend.class, args);
        devarea.bot.Init.initBot();

        Init.client.onDisconnect().block();
    }

}
