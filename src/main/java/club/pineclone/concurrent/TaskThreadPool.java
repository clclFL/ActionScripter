package club.pineclone.concurrent;

import java.util.concurrent.*;

public class TaskThreadPool {

    private TaskThreadPool(){}

    private static final ThreadPoolExecutor POOL_1 = new ThreadPoolExecutor(
            5 , 10 , 5 , TimeUnit.MINUTES , new LinkedBlockingQueue<>());
    private static final ExecutorService POOL_2 = Executors.newSingleThreadExecutor();

    public static ExecutorService getDefPool() {
        return POOL_1;
    }


}
