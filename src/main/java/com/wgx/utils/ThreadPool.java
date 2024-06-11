package com.wgx.utils;


import java.util.concurrent.*;

/**
 * 线程池
 *
 * @author sxyuser
 * @date 2023-03-08 15:01:17
 */
public class ThreadPool {

    private ThreadPool() {
    }

    /**
     * 核心池大小
     */
    private static final int CORE_POOL_SIZE  = Integer.max(Runtime.getRuntime().availableProcessors(), 5);

    /**
     * 线程池允许的最大线程数
     */
    private static final int MAXIMUM_POOL_SIZE = 16;
    /**
     * 空闲的多余线程最大存活时间,单位秒
     */
    private static final int KEEP_ALIVE_TIME = 60;
    /**
     * 任务阻塞队列大小
     */
    private static final int QUEUE_SIZE = 10000;

    /**
     * 线程名字
     */
    private static String threadName = "thread-pool-%d";


    private static volatile ThreadPoolExecutor threadPool;


    /**
     * 无返回值直接执行
     */
    public static void execute(Runnable runnable) {
        getThreadPool().execute(runnable);
    }

    /**
     * 返回值直接执行
     */
    public static <T> Future<T> submit(Callable<T> callable) {
        return getThreadPool().submit(callable);
    }

    /**
     * shutdown只是将线程池的状态设置为SHUTWDOWN状态，正在执行的任务会继续执行下去，没有被执行的则中断。
     */
    public static void shutdown() {
        if (!getThreadPool().isShutdown()) {
            getThreadPool().shutdown();
        }
    }

    /**
     * 线程池需要执行的任务数量。
     *
     * @return long
     */
    public static long getTaskCount() {
        return getThreadPool().getTaskCount();
    }

    /**
     * 线程池里曾经创建过的最大线程数量。通过这个数据可以知道
     * 线程池是否曾经满过。如该数值等于线程池的最大大小，则表示线程池曾经满
     * 过。
     *
     * @return long
     */
    public static long getLargestPoolSize() {
        return getThreadPool().getLargestPoolSize();
    }

    /**
     * 线程池的线程数量。如果线程池不销毁的话，线程池里的线程不会
     * 自动销毁，所以这个大小只增不减。
     *
     * @return long
     */
    public static long getPoolSize() {
        return getThreadPool().getPoolSize();
    }

    /**
     * 得到完成任务数
     *
     * @return long
     */
    public static long getCompletedTaskCount() {
        return getThreadPool().getCompletedTaskCount();
    }

    /**
     * 获取活动的线程数。
     *
     * @return long
     */
    public static long getActiveCount() {
        return getThreadPool().getActiveCount();
    }

    /**
     * shutdownNow则是将线程池的状态设置为STOP，正在执行的任务则被停止，没被执行任务的则返回。
     */
    public static void shutdownNow() {
        if (ThreadPool.getActiveCount() >= 0) {
            getThreadPool().shutdownNow();
            threadPool = null;
        }
    }


    /**
     * 获取线程池
     * ● corePoolSize 核心线程数目 (最多保留的线程数)
     * ● maximumPoolSize 最大线程数目
     * ● keepAliveTime 存活时间。如果当前线程池中的线程数量比核心线程数量多，
     * 并且是闲置状态，则这些闲置的线程能存活的最大时间。
     * ● unit 时间单位
     * ● workQueue 阻塞队列
     * ● threadFactory 线程工厂 - 可以为线程创建时起个好名字
     * ● handler 拒绝策略
     *
     * @return 线程池对象
     */
    public static ThreadPoolExecutor getThreadPool() {
        if (threadPool == null) {
            synchronized (ThreadPool.class) {
                if (threadPool == null) {
                    threadName = "thread-pool-%d";
                    threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(QUEUE_SIZE), Executors.defaultThreadFactory(),
                            new ThreadPoolExecutor.AbortPolicy());
                    //当核心线程空闲时则停止，否则核心线程一直会存在
                    //threadPool.allowCoreThreadTimeOut(true);
                }
            }
        }
        return threadPool;
    }


}

