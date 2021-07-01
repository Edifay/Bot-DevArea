package devarea;

import devarea.backend.SpringBackend;
import devarea.bot.Init;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;
import java.io.PrintStream;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
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
