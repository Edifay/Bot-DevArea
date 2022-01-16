package devarea.bot.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FunctionEvent {

    private static ExecutorService exe = Executors.newCachedThreadPool();

    public synchronized static void startAway(Runnable runnable) {
        exe.submit(runnable);
    }

}
