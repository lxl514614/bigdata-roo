package com.bigdata.java.queue;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class TestBlockingQueueProducer implements Runnable {

    private BlockingQueue<String> queue;

    Random random = new Random();

    public TestBlockingQueueProducer(BlockingQueue queue) {
        this.queue = queue;
    }

    public void run() {


        for (int i = 0; i< 10; i++) {

            try {
                Thread.sleep(random.nextInt(10));

                String task = Thread.currentThread().getName() + " made a message" + i;
                System.out.println(task);

                queue.put(task); // 阻塞方法

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
