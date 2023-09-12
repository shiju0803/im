/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ShareThreadPool类用于创建一个共享的线程池，用于处理多个任务。
 * 该线程池的核心线程数和最大线程数均为8，任务队列容量为2^20。
 * <p>
 * 当向线程池提交任务时，会在当前线程的堆栈信息中记录当前任务的调用栈信息。
 * 然后将任务提交到线程池中执行，并记录任务执行的开始时间。
 * <p>
 * 当任务执行完成后，记录任务执行的结束时间，并根据任务执行时间的长短输出相应的日志信息。
 * 如果任务执行时间超过1秒，则输出致命警告日志，如果超过300毫秒，则输出警告日志，否则输出debug日志。
 * <p>
 * 在输出日志信息时，会记录当前线程池中剩余未执行的任务数量，并输出相应的日志信息。
 */
@Slf4j
@Service
public class ShareThreadPool {

    private final ThreadPoolExecutor threadPoolExecutor;

    // 记录当前线程池中未执行的任务数量
    private final AtomicLong ind = new AtomicLong(0);

    /**
     * 初始化线程池，包括核心线程数、最大线程数、线程空闲时间、阻塞队列、线程工厂等参数的设置
     */ {
        final AtomicInteger tNum = new AtomicInteger(0);

        threadPoolExecutor =
                new ThreadPoolExecutor(8, 8, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>(2 << 20), r -> {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    t.setName("SHARE-Processor-" + tNum.getAndIncrement());
                    return t;
                });

    }

    /**
     * 向线程池提交任务，并在当前线程的堆栈信息中记录当前任务的调用栈信息。
     *
     * @param r 待执行的任务
     */
    public void submit(Runnable r) {
        // 获取当前线程的堆栈信息
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // 线程数自增
        ind.incrementAndGet();

        threadPoolExecutor.submit(() -> {
            // 记录开始时间
            long start = System.currentTimeMillis();
            try {
                r.run();
            } catch (Exception e) {
                log.error("ShareThreadPool_ERROR", e);
            } finally {
                // 记录结束时间
                long end = System.currentTimeMillis();
                // 计算任务执行时间
                long dur = end - start;
                // 线程数自减
                long andDecrement = ind.decrementAndGet();
                // 根据任务执行时间输出不同级别的日志信息
                if (dur > 1000) {
                    log.warn("ShareThreadPool executed taskDone,remanent num = {},slow task fatal warning,costs time ="
                                     + " {},stack: {}", andDecrement, dur, stackTrace);
                } else {
                    if (dur > 300) {
                        log.warn("ShareThreadPool executed taskDone,remanent num = {},slow task warning: {},costs time"
                                         + " = {},", andDecrement, r, dur);
                    } else {
                        log.debug("ShareThreadPool executed taskDone,remanent num = {}", andDecrement);
                    }
                }
            }
        });
    }
}