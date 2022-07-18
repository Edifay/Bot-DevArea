package devarea.bot.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FunctionEvent {

    private static final ExecutorService exe = Executors.newCachedThreadPool();

    public synchronized static void startAway(Runnable runnable) {
        exe.submit(runnable);
    }

    public synchronized static void repeatEachMillis(Runnable runnable, long time, boolean error) {
        exe.submit(() -> {
            try {
                while (true) {
                    runnable.run();
                    Thread.sleep(time);
                }
            } catch (Exception e) {
                if (error)
                    e.printStackTrace();
            }
        });
    }

    public synchronized static void  startAwayIn(Runnable runnable, long time, boolean error) {
        exe.submit(() -> {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                if (error)
                    e.printStackTrace();
            } finally {
                runnable.run();
            }
        });
    }

}
