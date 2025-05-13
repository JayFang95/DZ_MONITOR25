package com.dzkj.common.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/1/12
 * @description 线程池工具类
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class ThreadPoolUtil {

    /**
     * 核心线程数，会一直存活，即使没有任务，线程池也会维护线程的最少数量
     */
    private static final int SIZE_CORE_POOL = Runtime.getRuntime().availableProcessors();
    /**
     * 线程池维护线程的最大数量
     */
    private static final int SIZE_MAX_POOL = Runtime.getRuntime().availableProcessors();
    /**
     * 线程池维护线程所允许的空闲时间
     */
    private static final long ALIVE_TIME = 5000;
    /**
     * 线程缓冲队列
     */
    private static final BlockingQueue<Runnable> B_QUEUE = new ArrayBlockingQueue<Runnable>(100);
    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(
            SIZE_CORE_POOL,
            SIZE_MAX_POOL,
            ALIVE_TIME,
            TimeUnit.MILLISECONDS,
            B_QUEUE,
            new ThreadPoolExecutor.CallerRunsPolicy());
    private static final ThreadPoolExecutor CONTROL_BOX_POOL = new ThreadPoolExecutor(
            SIZE_CORE_POOL,
            SIZE_MAX_POOL,
            ALIVE_TIME,
            TimeUnit.MILLISECONDS,
            B_QUEUE,
            new ThreadPoolExecutor.CallerRunsPolicy());

    static {
        POOL.prestartAllCoreThreads();
        CONTROL_BOX_POOL.prestartAllCoreThreads();
    }

    public static ThreadPoolExecutor getPool() {
        return POOL;
    }
    public static ThreadPoolExecutor getBoxPool() {
        return CONTROL_BOX_POOL;
    }

}
