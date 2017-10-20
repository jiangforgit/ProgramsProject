package programs.publicmodule.core.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by caijiang.chen on 2017/10/20.
 */

public class PublicThreadPool {

    private static PublicThreadPool publicThreadPool;

    private static ThreadPoolExecutor poolExecutor;

    public static int corePoolSixe = 3;
    public static int maxPoolSize = 50;
    public static BlockingQueue<Runnable> workQueue ;

    private PublicThreadPool(){}

    public static PublicThreadPool getPool(){
        if(null == publicThreadPool) {
            publicThreadPool = new PublicThreadPool();
            poolExecutor = new ThreadPoolExecutor(corePoolSixe,maxPoolSize,12000,TimeUnit.MILLISECONDS,workQueue);
        }
        return publicThreadPool;
    }

    public void execute(Runnable runnable){
        poolExecutor.execute(runnable);
    }


}
