package programs.publicmodule.core.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by caijiang.chen on 2017/10/20.
 */

public class PublicThreadPool {

    private static PublicThreadPool publicThreadPool;
    //ThreadPoolExecutor
    public static int corePoolSixe = 3;
    public static int maxPoolSize = 8;
    public static final int QueueSize = 50;
    public static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(QueueSize);
    private ThreadPoolExecutor threadPoolExecutor;

    //ExecutorService
    private ExecutorService fixedThreadPool;
    private ExecutorService singleThreadExecutor;
    private ExecutorService cacheThreadPool;

    //ScheduledExecutorService  定时或周期任务
    private ScheduledExecutorService scheduledPool;
    private ScheduledExecutorService singleThreadScheduledPool;

    private PublicThreadPool(){}

    public static PublicThreadPool getPool(){
        if(null == publicThreadPool) {
            publicThreadPool = new PublicThreadPool();
        }
        return publicThreadPool;
    }

    public ThreadPoolExecutor getThreadPoolExecutor(){
        if(null == threadPoolExecutor){
            threadPoolExecutor = new ThreadPoolExecutor(corePoolSixe, maxPoolSize, 12000, TimeUnit.MILLISECONDS, workQueue, new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                    getSingleThreadExecutor().execute(runnable);
                }
            });
        }
        return threadPoolExecutor;
    }

    /**
    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue());
    }
     */
    public ExecutorService getFixedThreadPool(){
        if(null == fixedThreadPool)
            fixedThreadPool = Executors.newFixedThreadPool(corePoolSixe);
        return fixedThreadPool;
    }

    /**
    public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue()));
    }
     */
    public ExecutorService getSingleThreadExecutor(){
        if(null == singleThreadExecutor)
            singleThreadExecutor = Executors.newSingleThreadExecutor();
        return singleThreadExecutor;
    }

    /**
    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue());
    }
     */
    public ExecutorService getCacheThreadPool(){
        if(null == cacheThreadPool)
            cacheThreadPool = Executors.newCachedThreadPool();
        return cacheThreadPool;
    }


    public ScheduledExecutorService getScheduledPool(){
        if(null == scheduledPool)
            scheduledPool = Executors.newScheduledThreadPool(corePoolSixe);
        return scheduledPool;
    }

    public ScheduledExecutorService getSingleThreadScheduledPool(){
        if(null == singleThreadScheduledPool)
            singleThreadScheduledPool = Executors.newSingleThreadScheduledExecutor();
        return singleThreadScheduledPool;
    }

}
