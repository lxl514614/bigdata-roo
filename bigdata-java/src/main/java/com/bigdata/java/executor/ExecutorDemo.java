package com.bigdata.java.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程池使用demo
 */
public class ExecutorDemo {


    public static void main(String[] args) {

        /** 创建单线程线程池**/
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();

        /** 创建可回收的线程池 **/
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

        /** 创建固定线程数量的线程池 **/
        int cpuNum = Runtime.getRuntime().availableProcessors(); // 获取cpu数量
        System.out.println(cpuNum);
        ExecutorService newFixedT = Executors.newFixedThreadPool(cpuNum);  // 线程数量为cpu核数

        /** 创建可调度线程池 **/
        ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(cpuNum);

        /** 创建可调度单线程 线程池 **/
        ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();


    }
}
