package devarea;

import devarea.backend.SpringBackend;
import devarea.bot.Init;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws IOException, NoSuchFieldException, InterruptedException, IllegalAccessException {
        try {
            PrintStream out = new PrintStream("out.txt");
            System.setOut(out);
            System.setErr(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Logger.getLogger("io.netty").setLevel(Level.OFF);
        devarea.bot.Init.init();
        SpringApplication.run(SpringBackend.class, args);

        Init.client.onDisconnect().block();
    }
}
