package com.shirongbao.example;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: ShiRongbao
 * @date: 2025-03-25
 * @description:
 */
public class ThreadPoolTest {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10,
                500,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());

        // 提前启动全部核心线程
        executor.prestartAllCoreThreads();

        executor.execute(() -> {
            // 当execute时，只要当前线程池中的线程数 < 核心线程数，就会去创建新的线程
            System.out.println("Hello, World!");
        });

        executor.shutdown();

        // 线程池中的核心线程，执行完任务后，会尝试从阻塞队列中获取任务来保活

        // 如果阻塞队列中的任务满了，然后在有新的任务要执行，就会去创建新的线程，直到线程数 >= 最大线程数

        // 如果线程数 >= 最大线程数了，那么会执行拒绝策略，默认是抛出异常
        // 拒绝策略：1. 抛出异常（默认）2. 当前线程执行任务 3. 丢弃队列中最早的任务 4. 什么都不做（也就是丢弃当前任务）

        // 如果队列中没有任务了，那么线程尝试获取任务时：
        // 1. 如果当前线程数大于核心线程数（即非核心线程），并且在 keepAliveTime 时间内无法获取到任务，
        //    则该线程会被销毁。
        // 2. 核心线程不会因超时而被销毁（除非调用了 allowCoreThreadTimeOut(true)）。

        // 如果某个线程执行任务时出现了异常，那么这个线程会被销毁，同时异常会被抛出，再创建新的线程。抛出的异常可以被捕获到，执行对应的逻辑。
    }
}
