package com.bigdata.java.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolWithRunable {

    public static void main(String[] args) {

        // 可回收线程demo
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i< 5; i++) {

            newCachedThreadPool.execute(new Runnable() {
                public void run() {

                    System.out.println("thread name : "+ Thread.currentThread().getName());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        newCachedThreadPool.shutdown();
    }
}
