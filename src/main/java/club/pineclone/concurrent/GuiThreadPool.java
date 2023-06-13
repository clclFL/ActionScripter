package club.pineclone.concurrent;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GuiThreadPool {

    private static final ThreadPoolExecutor POOL_1 = new ThreadPoolExecutor(
            5 , 10 , 5 , TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    public static ThreadPoolExecutor getDefPool() {
        return POOL_1;
    }

}
