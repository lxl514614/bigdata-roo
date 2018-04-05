package com.bigdata.java.executor;


import java.util.concurrent.*;

public class ThreadPoolWithCallable {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        ExecutorService pool = Executors.newFixedThreadPool(2);

        for (int i = 0; i< 10; i++) {
            Future<String> submit = pool.submit(new Callable<String>() {
                public String call() throws Exception {

                    System.out.println("thread name : "+Thread.currentThread().getName());

                    Thread.sleep(1000);
                    System.out.println("thread name : "+Thread.currentThread().getName());

                    return "b--"+ Thread.currentThread().getName();
                }
            });

            System.out.println("获取结果:"+ submit.get());
        }

        pool.shutdown();
    }
}
