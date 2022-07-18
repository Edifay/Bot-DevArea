package devarea.global.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadHandler {

    private static final ExecutorService exe = Executors.newCachedThreadPool();
    /*
        start in another thread
     */
    public synchronized static void startAway(Runnable runnable) {
        exe.submit(runnable);
    }

    /*
        Use to loop an action every given time -> (time)
     */
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

    /*
        start in another thread with a start cooldown
     */
    public synchronized static void startAwayIn(Runnable runnable, long time, boolean error) {
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
